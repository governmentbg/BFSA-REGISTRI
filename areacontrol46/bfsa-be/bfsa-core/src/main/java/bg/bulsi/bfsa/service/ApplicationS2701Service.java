package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS2701DTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.EducationalDocumentType;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS2701;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.util.Constants;
import bg.bulsi.bfsa.util.ImageValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2701Service {
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final BranchService branchService;
    private final ClassifierService classifierService;
    private final MailService mailService;
    private final BaseApplicationService baseApplicationService;
    private final ServiceType SERVICE_TYPE = ServiceType.S2701;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002041_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2701DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2701DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS2701DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(REGISTER_CODE);
        List<String> errors = new ArrayList<>();

        Contractor applicant = baseApplicationService.buildApplicant(
                dto.getApplicantIdentifier(), EntityType.PHYSICAL,
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

        ApplicationS2701 application = ApplicationS2701.builder()
                .serviceRequestWrapper(wrapper)
                .requestorApplicantMatch(dto.getRequestorApplicantMatch())
                .educationalDocumentType(dto.getEducationalDocumentType())
                .educationalDocumentNumber(dto.getEducationalDocumentNumber())
                .educationalDocumentDate(dto.getEducationalDocumentDate())
                .educationalInstitution(dto.getEducationalInstitution())
                .description(dto.getDescription())
                .discrepancyUntilDate(dto.getDiscrepancyUntilDate())
                .certificateImage(dto.getCertificateImage())
                .status(ApplicationStatus.ENTERED).build();

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2701(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record education(final Long recordId, final ApplicationS2701DTO dto, final Language language) {
        if (recordId == null || recordId <= 0) {
            throw new RuntimeException("ID field is required");
        }
        if (!recordId.equals(dto.getRecordId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter recordId");
        }

        Record record = recordService.findById(recordId);

        ApplicationS2701 application = record.getApplicationS2701();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2701.class, "null");
        }

        application.setRequestorApplicantMatch(dto.getRequestorApplicantMatch());
        application.setEducationalDocumentType(dto.getEducationalDocumentType());
        application.setEducationalDocumentNumber(dto.getEducationalDocumentNumber());
        application.setEducationalDocumentDate(dto.getEducationalDocumentDate());
        application.setEducationalInstitution(dto.getEducationalInstitution());
        application.setDescription(dto.getDescription());
        application.setDiscrepancyUntilDate(dto.getDiscrepancyUntilDate());
        application.setCertificateImage(dto.getCertificateImage());

        record.setStatus(RecordStatus.PROCESSING);

        return recordRepository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final Language language) {
        Record record = recordService.findById(recordId);

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS2701 application = record.getApplicationS2701();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2701.class, "null");
        }

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .validUntilDate(EducationalDocumentType.TRAINING.equals(application.getEducationalDocumentType())
                        ? LocalDate.now().plusYears(10L)
                        : null)
                .record(record)
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .build();
        paper.getI18ns().add(
                new ContractorPaperI18n("ЗАЯВЛЕНИЕ за издаване на сертификат за използване на продукти " +
                        "за растителна защита от професионална категория на употреба по чл. 83 и чл. 87 от Закона за защита на растенията",
                        application.getDescription(),
                        application.getEducationalInstitution(),
                        paper,
                        language)
        );

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

        ApplicationS2701 application = record.getApplicationS2701();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2701.class, "null");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);

        ApplicationS2701 application = record.getApplicationS2701();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2701.class, "null");
        }

        record.setStatus(RecordStatus.FOR_CORRECTION);
        application.setStatus(ApplicationStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }

    @Transactional
    public Record attach(final Long recordId, final MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("The certificate file is empty");
        }
        long fileSizeInBytes = 350 * 1024;
        if (file.getSize() > fileSizeInBytes) {
            throw new RuntimeException("The certificate image file size is greater than " + fileSizeInBytes + "K");
        }
        try {
            if (ImageValidator.isPng(file.getBytes()) || ImageValidator.isJpg(file.getBytes())) {
                Record record = recordService.findById(recordId);

                ApplicationS2701 application = record.getApplicationS2701();
                if (application == null) {
                    throw new EntityNotFoundException(ApplicationS2701.class, "null");
                }
                application.setCertificateImage(file.getBytes());
                return recordRepository.save(record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}