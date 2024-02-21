package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS1811DTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS1811;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.Vehicle;
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
public class ApplicationS1811Service {
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final NomenclatureService nomenclatureService;
    private final BranchService branchService;
    private final ClassifierService classifierService;
    private final MailService mailService;
    private final BaseApplicationService baseApplicationService;
    private final FacilityService facilityService;
    private final VehicleService vehicleService;

    private final ServiceType SERVICE_TYPE = ServiceType.S1811;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002007_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    //  S1199
    @Transactional(readOnly = true)
    public ApplicationS1811DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS1811DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS1811DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        ApplicationS1811 application = ApplicationS1811.builder()
                .serviceRequestWrapper(wrapper)
                .facilityRegNumber(dto.getFacilityRegNumber())
                .commencementActivityDate(dto.getCommencementActivityDate())
                .status(ApplicationStatus.ENTERED).build();

        // Използвам МПС за транспортиране на храни по чл. 50
        if (!CollectionUtils.isEmpty(dto.getVehicleCertificateNumbers())) {
            application.getCh50vehicles().addAll(
                    baseApplicationService.getCh50Vehicles(dto.getVehicleCertificateNumbers(), errors, language)
            );
        }

        // Използвам МПС за транспортиране на храни по чл. 52
        if (!CollectionUtils.isEmpty(dto.getCh52Vehicles())) {
            for (VehicleDTO vehicleDTO : dto.getCh52Vehicles()) {
                if (!StringUtils.hasText(vehicleDTO.getRegistrationPlate())) {
                    errors.add("Can't register vehicle without registration plate");
                } else {
                    // check errors only
                    baseApplicationService.buildVehicle(vehicleDTO, register, branch, errors, language);
                    application.getCh52vehicles().add(vehicleDTO);
                }
            }
        }

        Facility facility = facilityService.findByRegNumberOrNull(dto.getFacilityRegNumber());

        if (facility == null) {
            errors.add("Facility with reg number " + dto.getFacilityRegNumber() + " does not exist.");
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS1811(application);
        record.setFacility(facility);
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

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS1811 application = record.getApplicationS1811();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS1811.class, "null");
        }

        Facility facility = record.getFacility() != null
                ? record.getFacility()
                : facilityService.findByRegNumber(application.getFacilityRegNumber());

        if (!Constants.ACTIVITY_TYPE_FOOD_WHOLESALE_CODE.equals(facility.getActivityType().getCode())) {
            throw new RuntimeException("The facility with regNumber " + facility.getRegNumber() +
                    " has no activity type FOOD_WHOLESALE");
        }

        if (!Constants.SUB_ACTIVITY_TYPE_WAREHOUSE_CODE.equals(facility.getSubActivityType().getCode())) {
            throw new RuntimeException("The facility with regNumber " + facility.getRegNumber() +
                    " has no sub activity type WAREHOUSE");
        }

        if (applicant.getContractorFacilities().stream().noneMatch(cf -> facility.getId().equals(cf.getFacility().getId())
                && SERVICE_TYPE.equals(cf.getServiceType()))) {
            applicant.addContractorFacility(facility, SERVICE_TYPE);
        }

        Nomenclature n = nomenclatureService.findByCode(Constants.SUB_ACTIVITY_TYPE_CUSTOMS_WAREHOUSE_CODE);

        if (!CollectionUtils.isEmpty(application.getCh50vehicles())) {
            for (VehicleDTO dto : application.getCh50vehicles()) {
                Vehicle vehicle = vehicleService.findByRegistrationPlate(dto.getRegistrationPlate(), language);
                vehicle.setEnabled(true);

                if (applicant.getContractorVehicles().stream()
                        .noneMatch(cf -> vehicle.getId().equals(cf.getVehicle().getId()))) {
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, null);
                }
            }
        }

        if (!CollectionUtils.isEmpty(application.getCh52vehicles())) {
            for (VehicleDTO dto : application.getCh52vehicles()) {
                String regPlate = dto.getRegistrationPlate();
                if (StringUtils.hasText(regPlate)) {
                    Vehicle vehicle = vehicleService.findByRegistrationPlateOrNull(regPlate, language);
                    if (vehicle == null) {
                        vehicle = VehicleDTO.to(dto, language);
                        vehicle.setVehicleType(nomenclatureService.findByCode(dto.getVehicleTypeCode()));
                    }
                    vehicle.setEnabled(true);
                    if (applicant.getContractorVehicles().stream()
                            .noneMatch(cf -> regPlate.equals(cf.getVehicle().getRegistrationPlate()))) {
                        applicant.addContractorVehicle(vehicle, SERVICE_TYPE, nomenclatureService.findByCode(dto.getVehicleOwnershipTypeCode()));
                    }
                }
            }
        }

        facility.setSubActivityType(n);

        Classifier register = classifierService.findByCode(REGISTER_CODE);
        if (facility.getRegisters().stream().noneMatch(c -> register.getCode().equals(c.getCode()))) {
            facility.getRegisters().add(register);
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
        record.getApplicationS1811().setStatus(ApplicationStatus.REJECTED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message,
                                final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        mailService.sendApplicationCorrectionNotification(record, message, language);
        return recordRepository.save(record);

//        ContractorPaper paper = record.getContractorPaper();
//        // TODO: Check paper and throw exception *DONE
//        if (paper == null || !ApprovalDocumentStatus.ENTERED.equals(paper.getStatus())) {
//            throw new EntityNotFoundException(FacilityPaper.class,
//                    SERVICE_TYPE_S1811 + "/" + ApprovalDocumentStatus.ENTERED);
//        }
//        paper.setStatus(ApprovalDocumentStatus.DISCREPANT);
//        paper.setDiscrepancyUntilDate(dto.getDiscrepancyUntilDate());
//
//        ContractorPaperI18n i18n = paper.getI18n(language);
//        i18n.setDescription(dto.getDescription());
//
//        record.setStatus(RecordStatus.PROCESSING);
//
//        return recordRepository.save(record);
    }
}