package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS502DTO;
import bg.bulsi.bfsa.dto.ApproveDTO;
import bg.bulsi.bfsa.dto.DaeuRegisterPaymentResponseDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS502;
import bg.bulsi.bfsa.model.ApplicationS503;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS502Service {

    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordService recordService;
    private final RecordRepository recordRepository;
    private final NomenclatureService nomenclatureService;
    private final EPaymentService ePaymentProdService;
    private final MailService mailService;

    private final ServiceType SERVICE_TYPE = ServiceType.S502;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002029_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS502DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS502DTO.ofRecord(recordService.findById(recordId), language);
    }

//    @Transactional(readOnly = true)
//    public List<ApplicationS502DTO> findAllByApplicantIdAndStatus(final Long applicantId, final ApplicationStatus status, final Language language) {
//        return ApplicationS502DTO.of(repository.findAllByApplicant_IdAndStatus(applicantId, status), language);
//    }

    @Transactional
    public Record register(final ApplicationS502DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        ApplicationS502 application = ApplicationS502.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED)
                .testingAddresses(dto.getTestingAddresses())
                .pppTestingPersons(dto.getPppTestingPersons())
                .easedFacilities(dto.getEasedFacilities())
                .maintenanceEquipments(dto.getMaintenanceEquipments())
                .testMethodologies(dto.getTestMethodologies())
                .researchPlansDescription(dto.getResearchPlansDescription())
                .archivingDocDescription(dto.getArchivingDocDescription())
                .build();

        if (!CollectionUtils.isEmpty(dto.getPlantGroupTypeCodes())) {
            for (String code : dto.getPlantGroupTypeCodes()) {
                application.getPlantGroupTypes().add(nomenclatureService.findByCode(code));
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);

        record.setApplicationS502(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record sendForPayment(final Long recordId, final BigDecimal price, final Language language) throws Exception {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price can't be empty");
        }

        Record record = recordService.findById(recordId);

        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        ApplicationS502 application = record.getApplicationS502();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS503.class, "null");
        }

        application.setPrice(price);

        // TODO Pass DaeuRegisterPaymentResponseDTO fields to the email template
        DaeuRegisterPaymentResponseDTO responseDTO = ePaymentProdService.registerEPayment(record); // TODO pass amount to register payment

        mailService.sendApplicationPaymentNotification(price, record, language, responseDTO.getAccessCode());

        application.setStatus(ApplicationStatus.PAYMENT_CONFIRMATION); // TODO: Добавяне на "За потвърждение на плащане от финансист"

        return recordRepository.save(record);
    }

    @Transactional
    public Record confirmPayment(final Long recordId) {
        Record record = recordService.findById(recordId);

        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        ApplicationS502 application = record.getApplicationS502();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS503.class, "null");
        }

        if (!ApplicationStatus.PAYMENT_CONFIRMATION.equals(application.getStatus())) {
            throw new RuntimeException("Application status have to be " + ApplicationStatus.PAYMENT_CONFIRMATION + " to confirm application payment, but it is "
                    + application.getStatus());
        }

        application.setStatus(ApplicationStatus.PAYMENT_CONFIRMED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final ApproveDTO dto, final Language language) {
        Record record = recordService.findById(recordId);

        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS502 application = record.getApplicationS502();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS503.class, "null");
        }

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .orderNumber(dto.getOrderNumber())
                .orderDate(dto.getOrderDate())
                .validUntilDate(dto.getOrderDate().plusYears(10L))
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .record(record)
                .build();
        paper.getI18ns().add(
                new ContractorPaperI18n(SERVICE_TYPE.name(),
                        "ЗАЯВЛЕНИЕ за одобряване на бази за извършване на биологично изпитване на продукти за растителна защита по чл.74, ал.. 1 от Закона за защита на растенията и чл.22, ал. 4 от Наредба № 19 от 8 ноември 2016 г за биологично изпитване на продукти за растителна защита",
                        null, paper, language)
        );

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());

        applicant.getContractorPapers().add(paper);
        record.setContractorPaper(paper);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordRepository.save(record);
    }
}


