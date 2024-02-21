package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.ApplicationS3201DTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS3125;
import bg.bulsi.bfsa.model.ApplicationS3201;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.Settlement;
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
public class ApplicationS3201Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordService recordService;
    private final MailService mailService;
    private final SettlementService settlementService;

    private final ServiceType SERVICE_TYPE = ServiceType.S3201;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002042_CODE;
    public static final String DIRECTORATE_CODE = Constants.PHYTO_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS3201DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS3201DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS3201DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        ApplicationS3201 application = ApplicationS3201.builder()
                .activityAddresses(dto.getActivityAddresses())
                .activityTypes(dto.getActivityTypes())
                .plantProducts(dto.getPlantProducts())
                .contactPersons(dto.getContactPersons())
                .plantPassportIssue(dto.getPlantPassportIssue())
                .markingIssue(dto.getMarkingIssue())
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS3201(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordService.save(record);
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
        ApplicationS3201 application = record.getApplicationS3201();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3201.class, "null");
        }

        if (!CollectionUtils.isEmpty(application.getActivityAddresses())) {
            for (AddressBO bo : application.getActivityAddresses()) {
                Settlement region = settlementService.findRegionBySettlementCode(bo.getSettlementCode());
                Branch branch = branchService.findBySettlementCode(region.getCode());
                bo.setRegNumber(baseApplicationService.generateNumber(branch.getSequenceNumber()));
            }
        }

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .regDate(LocalDate.now())
                .validUntilDate(null) // TODO: Check where is endDate.
//                .validUntilDate(EducationalDocumentType.TRAINING.equals(application.getEducationalDocumentType())
//                        ? LocalDate.now().plusYears(10L)
//                        : null)
                .record(record)
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .build();
        paper.getI18ns().add(
                new ContractorPaperI18n("Официален Регистър на професионалните оператори - лицата по чл. 6, ал. 1, т. 11 от ЗЗР, " +
                        "включително лицата, които внасят, произвеждат, преработват и/или отглеждат растения и растителни продукти, " +
                        "както и събирателните и разпределителните центрове, стоковите тържища и пазарите на производители " +
                        "на такива растения и растителни продукти",
                        null,
                        paper,
                        language)
        );

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());

        applicant.getContractorPapers().add(paper);
        record.setContractorPaper(paper);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordService.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }
        ApplicationS3125 application = record.getApplicationS3125();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3125.class, "null");
        }
        if (!CollectionUtils.isEmpty(application.getFoodSupplements())) {
            application.getFoodSupplements().forEach(fs -> fs.setEnabled(false));
        }

        // TODO implement me ...

        application.setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordService.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        ApplicationS3201 application = record.getApplicationS3201();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3201.class, "null");
        }

        // TODO implement me ...

        application.setStatus(ApplicationStatus.FOR_CORRECTION);
        record.setStatus(RecordStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordService.save(record);
    }

}


