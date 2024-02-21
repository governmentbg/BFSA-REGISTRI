package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS2698DTO;
import bg.bulsi.bfsa.dto.ApproveDTO;
import bg.bulsi.bfsa.dto.ContractorDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS2698;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.ApplicationS2698Repository;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2698Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final NomenclatureService nomenclatureService;
    private final RecordService recordService;
    private final ApplicationS2698Repository repository;
    private final MailService mailService;
    private final ContractorService contractorService;
    private final SettlementService settlementService;

    private final ServiceType SERVICE_TYPE = ServiceType.S2698;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002031_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2698DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2698DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional(readOnly = true)
    public List<ApplicationS2698DTO> findAllByApplicantIdAndStatus(final Long applicantId, final ApplicationStatus status, final Language language) {
        return ApplicationS2698DTO.of(repository.findAllByRecord_Applicant_IdAndStatus(applicantId, status), language);
    }

    @Transactional
    public Record register(final ApplicationS2698DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        List<String> errors = new ArrayList<>();
        Classifier register = classifierService.findByCode(REGISTER_CODE);

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

        ApplicationS2698 application = ApplicationS2698.builder()
                .adjuvantName(dto.getAdjuvantName())
                .adjuvantNameLat(dto.getAdjuvantNameLat())
                .manufacturerIdentifier(dto.getManufacturerIdentifier())
                .manufacturerName(dto.getManufacturerName())
                .effects(dto.getEffects())
                .serviceRequestWrapper(wrapper).build();

        Contractor supplier = contractorService.findByIdentifierOrNull(dto.getSupplier().getIdentifier());
        if (supplier != null) {
            String errMessagePrefix = "Supplier with identifier: " + supplier.getIdentifier();
            if (!StringUtils.hasText(supplier.getFullName())
                    && StringUtils.hasText(dto.getSupplier().getFullName())) {
                supplier.setFullName(dto.getSupplier().getFullName());
            } else if (StringUtils.hasText(dto.getSupplier().getFullName())
                    && !dto.getSupplier().getFullName().equals(supplier.getFullName())) {
                errors.add(errMessagePrefix + " has name: " + supplier.getFullName() +
                        " which is different than the name (" +
                        dto.getSupplier().getFullName() +
                        ") received from application");
            }
        } else {
            supplier = ContractorDTO.to(dto.getSupplier());
        }

        Address supplierAddress = AddressDTO.to(dto.getSupplier().getAddress());
        supplierAddress.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_SUPPLIER_CODE));
        supplierAddress.setServiceType(SERVICE_TYPE);
        supplierAddress.setSettlement(settlementService.findByCode(dto.getSupplier().getAddress().getSettlementCode()));
        supplier.getAddresses().add(supplierAddress);

        application.setSupplier(supplier);

        dto.getPppFunctionCodes().forEach(u -> application.getPppFunctions().add(nomenclatureService.findByCode(u)));

        application.setAdjuvantProductFormulationType(
                nomenclatureService.findByCode(dto.getAdjuvantProductFormulationTypeCode()));

        application.setIngredients(dto.getIngredients());
        application.setApplications(dto.getApplications());

        application.setStatus(ApplicationStatus.ENTERED);

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2698(application);
        application.setRecord(record);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final ApproveDTO dto) {
        Record record = recordService.findById(recordId);

        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS2698 application = record.getApplicationS2698();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2698.class, "null");
        }

        application.setOrderNumber(dto.getOrderNumber());
        application.setOrderDate(dto.getOrderDate());

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS2698().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.getApplicationS2698().setStatus(ApplicationStatus.FOR_CORRECTION);
        record.setStatus(RecordStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }
}
