package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS1590DTO;
import bg.bulsi.bfsa.dto.ApproveDTO;
import bg.bulsi.bfsa.dto.PersonBO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS1590;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.ApplicationS1590Repository;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS1590Service {

    private final ApplicationS1590Repository repository;
    private final BranchService branchService;
    private final SettlementService settlementService;
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final MailService mailService;
    private final BaseApplicationService baseApplicationService;
    private final ClassifierService classifierService;
    private final ServiceType SERVICE_TYPE = ServiceType.S1590;

    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002037_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS1590DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS1590DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional(readOnly = true)
    public List<ApplicationS1590DTO> findAllByApplicantIdAndStatus(final Long applicantId, final ApplicationStatus status, final Language language) {
        return ApplicationS1590DTO.of(repository.findAllByRecord_Applicant_IdAndStatus(applicantId, status), language);
    }

    @Transactional
    public Record register(final ApplicationS1590DTO dto, final ServiceRequestWrapper wrapper, Language language) {
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

        ApplicationS1590 application = ApplicationS1590.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();

        application.setWarehouseAddress(Address.builder()
                .phone(dto.getWarehouseAddress().getPhone())
                .mail(dto.getWarehouseAddress().getMail())
                .url(dto.getWarehouseAddress().getUrl())
                .settlement(settlementService.findByCode(dto.getWarehouseAddress().getSettlementCode()))
                .fullAddress(settlementService.getInfo(dto.getWarehouseAddress().getSettlementCode(), language.getLanguageId()))
                .address(dto.getWarehouseAddress().getAddress())
                .fullAddress(dto.getWarehouseAddress().getFullAddress())
                .build()
        );

        Contractor fumigationProcessControlPerson = baseApplicationService.buildContractor(dto.getCh83CertifiedPerson(), errors);
        baseApplicationService.hasPaperCheck(fumigationProcessControlPerson, errors, ServiceType.S2701);
        application.setCh83CertifiedPerson(fumigationProcessControlPerson);

        if (!CollectionUtils.isEmpty(dto.getCh83CertifiedPersons())) {
            for (PersonBO p : dto.getCh83CertifiedPersons()) {
                baseApplicationService.checkPerson(p, requestor, applicant, errors);
                application.getCh83CertifiedPersons().add(p);
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS1590(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

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

        ApplicationS1590 application = record.getApplicationS1590();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS1590.class, "null");
        }

        if (application.getCh83CertifiedPerson() == null) {
            throw new RuntimeException("There are no Ch. 83 certified person");
        }

        if (baseApplicationService.hasNoPaper(application.getCh83CertifiedPerson(), ServiceType.S2701)) {
            throw new RuntimeException("Person with identifier: " + application.getCh83CertifiedPerson().getIdentifier() +
                    " doesn't have " + ServiceType.S2701 + " certificate.");
        }

        if (CollectionUtils.isEmpty(application.getCh83CertifiedPersons())) {
            throw new RuntimeException("There are no Ch. 83 certified persons");
        }

        for (PersonBO personBO : application.getCh83CertifiedPersons()) {
            if (baseApplicationService.hasNoPaper(personBO.getIdentifier(), ServiceType.S2701)) {
                throw new RuntimeException("Person with identifier: " + personBO.getIdentifier() + " doesn't have " +
                        ServiceType.S2701 + " certificate.");
            }
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
        record.getApplicationS1590().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);
        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
          Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        record.getApplicationS1590().setStatus(ApplicationStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }
}
