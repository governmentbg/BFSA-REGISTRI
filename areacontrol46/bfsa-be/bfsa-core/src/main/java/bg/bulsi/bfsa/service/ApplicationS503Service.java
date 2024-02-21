package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS503DTO;
import bg.bulsi.bfsa.dto.PersonBO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS503;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.ApplicationS503Repository;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS503Service {

    private final ApplicationS503Repository repository;
    private final BranchService branchService;
    private final SettlementService settlementService;
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final MailService mailService;
    private final BaseApplicationService baseApplicationService;
    private final ClassifierService classifierService;
    private final ServiceType SERVICE_TYPE = ServiceType.S503;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002033_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS503DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS503DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional(readOnly = true)
    public List<ApplicationS503DTO> findAllByApplicantIdAndStatus(
            final Long applicantId,
            final ApplicationStatus status,
            final Language language) {
        return ApplicationS503DTO.of(repository.findAllByRecord_Applicant_IdAndStatus(applicantId, status), language);
    }

    @Transactional
    public Record register(final ApplicationS503DTO dto, final ServiceRequestWrapper wrapper, Language language) {
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
                && !Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_EXTERNAL_CODE
                .equals(dto.getRequestorAuthorTypeExternalCode())
                && !dto.getApplicantIdentifier().equals(dto.getRequestorIdentifier())) {
            requestor = baseApplicationService.buildRequestor(
                    dto.getRequestorIdentifier(), dto.getRequestorFullName(),
                    dto.getRequestorEmail(), dto.getRequestorPhone(),
                    dto.getRequestorCorrespondenceAddress(), errors, SERVICE_TYPE, language);
        }

        String applicationFullAddress = settlementService.getInfo(dto.getSettlementCode(), language.getLanguageId())
                + ", " + dto.getAddress();

        ApplicationS503 application = ApplicationS503.builder()
                .fullAddress(applicationFullAddress)
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();

        if (dto.getCh83CertifiedPerson() != null) {
            Contractor activityResponsiblePerson = baseApplicationService
                    .buildContractor(dto.getCh83CertifiedPerson(), errors);
            baseApplicationService.hasPaperCheck(activityResponsiblePerson, errors, ServiceType.S2701);
            application.setCh83CertifiedPerson(activityResponsiblePerson);
        }

        if (!CollectionUtils.isEmpty(dto.getCh83CertifiedPersons())) {
            for (PersonBO p : dto.getCh83CertifiedPersons()) {
                baseApplicationService.checkPerson(p, requestor, applicant, errors);
                application.getCh83CertifiedPersons().add(p);
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS503(application);
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

        ApplicationS503 application = record.getApplicationS503();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS503.class, "null");
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

        ContractorPaper contractorPaper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .status(ApprovalDocumentStatus.ACTIVE)
                .record(record)
                .enabled(true)
                .regNumber(UUID.randomUUID().toString())
                .regDate(LocalDate.now())
                .validUntilDate(LocalDate.now().plusYears(10L))
                .build();

        contractorPaper.getI18ns().add(new ContractorPaperI18n(
                "Национален електронен регистър на лицата, които притеж удостоверение за" +
                        " преопаковане на продукти за растителна защита, и на съответните обекти" +
                        " за преопаковане на продукти за растителна защита",
                null,
                contractorPaper,
                language)
        );

        record.setContractorPaper(contractorPaper);

        applicant.getContractorPapers().add(contractorPaper);

        baseApplicationService.generateRegNumber(contractorPaper, record.getBranch().getSequenceNumber());

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
        ApplicationS503 application = record.getApplicationS503();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS503.class, "null");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        record.getApplicationS503().setStatus(ApplicationStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);
        return recordRepository.save(record);
    }
}
