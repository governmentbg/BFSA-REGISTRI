package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.ApplicationS2697DTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS2697;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2697Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final SettlementService settlementService;
    private final RecordService recordService;
    private final NomenclatureService nomenclatureService;
    private final CountryService countryService;
    private final RecordRepository recordRepository;
    private final MailService mailService;

    private final ServiceType SERVICE_TYPE = ServiceType.S2697;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002035_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2697DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2697DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS2697DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(REGISTER_CODE);
        List<String> errors = new ArrayList<>();

        Contractor applicant = baseApplicationService.buildApplicant(
                dto.getApplicantIdentifier(), dto.getEntityType(),
                dto.getApplicantFullName(), dto.getApplicantEmail(),
                dto.getApplicantPhone(), branch,
                dto.getApplicantCorrespondenceAddress(),
                register, errors, SERVICE_TYPE, language);

        Contractor requestor = applicant;

        if (!Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_CODE.equals(dto.getRequestorAuthorTypeCode())
                && !Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_EXTERNAL_CODE.equals(dto.getRequestorAuthorTypeExternalCode())
                && !dto.getApplicantIdentifier().equals(dto.getRequestorIdentifier())) {
            requestor = baseApplicationService.buildRequestor(
                    dto.getRequestorIdentifier(), dto.getRequestorFullName(),
                    dto.getRequestorEmail(), dto.getRequestorPhone(),
                    dto.getRequestorCorrespondenceAddress(), errors, SERVICE_TYPE, language);
        }

        ApplicationS2697 application = ApplicationS2697.builder()
                .serviceRequestWrapper(wrapper)
                .warehouseAddress(dto.getWarehouseAddress())
                .status(ApplicationStatus.ENTERED)
                .build();

        if (!CollectionUtils.isEmpty(dto.getSubstances())) {
            application.setSubstances(dto.getSubstances());
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2697(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final Language language) {
        Record record = recordService.findById(recordId);

        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS2697 application = record.getApplicationS2697();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2697.class, "null");
        }
        // Add warehouse address
        if (application.getWarehouseAddress() != null) {
            Address address = AddressBO.to(application.getWarehouseAddress());
//            TODO Should we add a new address type?
            address.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE));
            address.setServiceType(SERVICE_TYPE);
            address.setCountry(application.getWarehouseAddress().getCountryCode() != null
                    ? countryService.findByCode(application.getWarehouseAddress().getCountryCode())
                    : null);
            address.setSettlement(application.getWarehouseAddress().getSettlementCode() != null
                    ? settlementService.findByCode(application.getWarehouseAddress().getSettlementCode())
                    : null);
            applicant.getAddresses().add(address);
        }

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .record(record)
                .regDate(LocalDate.now())
                // TODO Is the validity 10 years?
                .validUntilDate(LocalDate.now().plusYears(10L))
                .build();
        paper.getI18ns().add(
                        new ContractorPaperI18n(SERVICE_TYPE.name(),
                                "ЗАЯВЛЕНИЕ за издаване на удостоверение по чл. 41, ал. 1 от Закона за защита на" +
                                        " растенията за внос или въвеждане на неодобрени активни вещества",
                                null, paper, language)
                );


        record.setContractorPaper(paper);
        applicant.getContractorPapers().add(paper);

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());

        applicant.getContractorPapers().add(paper);
        record.setContractorPaper(paper);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }
        ApplicationS2697 application = record.getApplicationS2697();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2697.class, "null");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        record.getApplicationS2697().setStatus(ApplicationStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);
        return recordRepository.save(record);
    }
}


