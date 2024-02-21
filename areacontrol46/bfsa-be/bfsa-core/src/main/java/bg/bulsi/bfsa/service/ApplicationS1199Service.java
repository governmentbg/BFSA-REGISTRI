package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS1199DTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS1199;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorFacility;
import bg.bulsi.bfsa.model.Facility;
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
public class ApplicationS1199Service {
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final BranchService branchService;
    private final ClassifierService classifierService;
    private final MailService mailService;
    private final BaseApplicationService baseApplicationService;
    private final FacilityService facilityService;
    private final ServiceType SERVICE_TYPE = ServiceType.S1199;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002022_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    //  S1199
    @Transactional(readOnly = true)
    public ApplicationS1199DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS1199DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS1199DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(REGISTER_CODE);
        List<String> errors = new ArrayList<>();

        Contractor requestor = baseApplicationService.buildRequestor(
                dto.getRequestorIdentifier(), dto.getRequestorFullName(),
                dto.getRequestorEmail(), dto.getRequestorPhone(),
                dto.getRequestorCorrespondenceAddress(), errors, SERVICE_TYPE, language);

        Contractor applicant = baseApplicationService.buildApplicant(
                dto.getApplicantIdentifier(), dto.getEntityType(),
                dto.getApplicantFullName(), dto.getApplicantEmail(),
                dto.getApplicantPhone(), branch,
                dto.getApplicantCorrespondenceAddress(),
                register, errors, SERVICE_TYPE, language);

        ApplicationS1199 application = ApplicationS1199.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();

        Facility facility = facilityService.findByRegNumberOrNull(dto.getFacilityRegNumber());

        if (facility == null) {
            errors.add("Facility with registration number " + dto.getFacilityRegNumber() + " does not exist.");
        }
        application.setLeasedWarehouseSpace(dto.getLeasedWarehouseSpace());
        application.setFacilityRegNumber(dto.getFacilityRegNumber());

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS1199(application);
        record.setFacility(facility);
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

        Contractor applicant = record.getApplicant();

        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS1199 application = record.getApplicationS1199();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS1199.class, "null");
        }

        Facility facility = record.getFacility() != null
                ? record.getFacility()
                : facilityService.findByRegNumber(application.getFacilityRegNumber());

        Classifier register = classifierService.findByCode(REGISTER_CODE);
        if (facility.getRegisters().stream().noneMatch(c -> register.getCode().equals(c.getCode()))) {
            facility.getRegisters().add(register);
        }

        List<ContractorFacility> contractorFacilities = applicant.getContractorFacilities().stream()
                .filter(cf -> facility.getId().equals(cf.getFacility().getId())
                        && SERVICE_TYPE.equals(cf.getServiceType())).toList();

        if (CollectionUtils.isEmpty(contractorFacilities)) {
            applicant.addContractorFacility(facility, SERVICE_TYPE, null, application.getLeasedWarehouseSpace());
        } else if (contractorFacilities.size() == 1) {
            contractorFacilities.get(0).setLeasedWarehouseSpace(application.getLeasedWarehouseSpace());
        } else {
            throw new RuntimeException("The contractor with identifier: " + applicant.getIdentifier() +
                    " has more than one facility with regNumber: " + facility.getRegNumber() + " for service type " + SERVICE_TYPE);
        }

        if (record.getFacility() == null) {
            record.setFacility(facility);
        }
        record.setStatus(RecordStatus.FINAL_APPROVED);
        application.setStatus(ApplicationStatus.APPROVED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FINAL_REJECTED);
        record.getApplicationS1199().setStatus(ApplicationStatus.REJECTED);


        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message,
                                final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        mailService.sendApplicationCorrectionNotification(record, message, language);
        return recordRepository.save(record);
    }
}