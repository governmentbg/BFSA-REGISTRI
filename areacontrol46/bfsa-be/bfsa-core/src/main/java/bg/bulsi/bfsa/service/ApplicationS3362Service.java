package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS3362DTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS3362;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS3362Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final NomenclatureService nomenclatureService;
    private final RecordService recordService;

    private final ServiceType SERVICE_TYPE = ServiceType.S3362;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002028_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS3362DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS3362DTO.ofRecord(recordService.findById(recordId), language);
    }
    
    
    @Transactional
    public Record register(final ApplicationS3362DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        List<String> errors = new ArrayList<>();
        Classifier register = classifierService.findByCode(REGISTER_CODE);

        Contractor applicant = baseApplicationService.buildApplicant(
                dto.getApplicantIdentifier(), dto.getEntityType(),
                dto.getApplicantFullName(), dto.getApplicantEmail(),
                dto.getApplicantPhone(), branch,
                dto.getApplicantCorrespondenceAddress(),
                register, errors, SERVICE_TYPE, language);

        baseApplicationService.hasPaperCheck(applicant, errors, ServiceType.S503);

        Contractor requestor = applicant;

        if (!Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_CODE.equals(dto.getRequestorAuthorTypeCode())
                && !Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_EXTERNAL_CODE.equals(dto.getRequestorAuthorTypeExternalCode())
                && !dto.getApplicantIdentifier().equals(dto.getRequestorIdentifier())) {
            requestor = baseApplicationService.buildRequestor(
                    dto.getRequestorIdentifier(), dto.getRequestorFullName(),
                    dto.getRequestorEmail(), dto.getRequestorPhone(),
                    dto.getRequestorCorrespondenceAddress(), errors, SERVICE_TYPE, language);
        }

        ApplicationS3362 application = ApplicationS3362.builder()
                .serviceRequestWrapper(wrapper)
                .registrationDate(dto.getRegistrationDate())
                .fullName(dto.getFullName())
                .contractStartDate(dto.getContractStartDate())
                .contractEndDate(dto.getContractEndDate())
                .pppName(dto.getPppName())
                .pppManufacturerName(dto.getPppManufacturerName())
                .pppPackageVolume(dto.getPppPackageVolume())
                .pppPackageUnitCode(nomenclatureService.findByCode(dto.getPppPackageUnitCode()))
                .pppPackageMaterial(dto.getPppPackageMaterial())
                .packageVolume(dto.getPackageVolume())
                .packageUnitCode(nomenclatureService.findByCode(dto.getPackageUnitCode()))
                .packageMaterial(dto.getPackageMaterial())
                .build();

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS3362(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }
        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }
        ApplicationS3362 application = record.getApplicationS3362();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3362.class, "null");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordService.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);

        ApplicationS3362 application = record.getApplicationS3362();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3362.class, "null");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordRepository.save(record);
    }
}


