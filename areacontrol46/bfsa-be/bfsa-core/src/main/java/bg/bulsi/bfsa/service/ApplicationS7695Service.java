package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS7695DTO;
import bg.bulsi.bfsa.dto.ApplicationS769DTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS7695;
import bg.bulsi.bfsa.model.ApplicationS7695Vehicle;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.Vehicle;
import bg.bulsi.bfsa.repository.ApplicationS7695Repository;
import bg.bulsi.bfsa.repository.ContractorRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS7695Service {
    private final ApplicationS7695Repository repository;
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordService recordService;
    private final VehicleService vehicleService;
    private final NomenclatureService nomenclatureService;
    private final RecordRepository recordRepository;
    private final MailService mailService;
    private final FacilityService facilityService;
    private final ContractorService contractorService;
    private final ContractorRepository contractorRepository;
    private final SettlementService settlementService;

    private final ServiceType SERVICE_TYPE = ServiceType.S7695;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002012_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS769DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS7695DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional(readOnly = true)
    public List<ApplicationS7695DTO> findAllByApplicantIdAndStatus(final Long applicantId, final ApplicationStatus status, final Language language) {
        return ApplicationS7695DTO.of(repository.findAllByRecord_Applicant_IdAndStatus(applicantId, status), language);
    }

    @Transactional
    public Record register(final ApplicationS7695DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        ApplicationS7695 application = ApplicationS7695.builder()
                .serviceRequestWrapper(wrapper)
                .foodTypeDescription(dto.getFoodTypeDescription())
                .capacityUsage(dto.getCapacityUsage())
                .facilityActivityDescription(dto.getFacilityActivityDescription())
                .status(ApplicationStatus.ENTERED)
                .unitType(StringUtils.hasText(dto.getUnitTypeCode())
                        ? nomenclatureService.findByCode(dto.getUnitTypeCode())
                        : null)
                .periodType(StringUtils.hasText(dto.getPeriodTypeCode())
                        ? nomenclatureService.findByCode(dto.getPeriodTypeCode())
                        : null)
                .commencementActivityDate(dto.getCommencementActivityDate())
                .build();

        Facility facility = facilityService.findByRegNumberOrNull(dto.getFacilityRegNumber());
        if (facility != null) {
            application.setFacility(facility);
        } else {
            errors.add("Facility with registration number " + dto.getFacilityRegNumber() + " does not exist.");
        }

        // Check if the foodType classifier exists and add its parents
        if (!CollectionUtils.isEmpty(dto.getFoodTypes())) {
            Set<Classifier> foodTypesWithParent = new HashSet<>();
            for (KeyValueDTO keyValue : dto.getFoodTypes()) {
                Classifier foodType = classifierService.findByCode(keyValue.getCode());
                foodTypesWithParent.add(foodType);
            }
            application.setApplicationS7695FoodTypes(KeyValueDTO.ofClassifiers(foodTypesWithParent, language));
        }

        // Използвам МПС за транспортиране на храни по чл. 50
        if (!CollectionUtils.isEmpty(dto.getCh50VehicleCertNumbers())) {
            for (String number : dto.getCh50VehicleCertNumbers()) {
                if (application.getApplicationS7695Vehicles().stream().noneMatch(v -> number.equals(v.getVehicle().getCertificateNumber()))) {
                    Vehicle vehicle = vehicleService.findByCertificateNumberOrNull(number, language);
                    if (vehicle != null) {
                        application.addApplicationS7695Vehicle(vehicle, null);
                    } else {
                        errors.add("Vehicle with certificate number: " + number + " doesn't exist.");
                    }
                }
            }
        }
        // Използвам МПС за транспортиране на храни по чл. 52
        if (!CollectionUtils.isEmpty(dto.getVehicles())) {
            for (VehicleDTO vehicleDTO : dto.getVehicles()) {
                if (!StringUtils.hasText(vehicleDTO.getRegistrationPlate())) {
                    throw new RuntimeException("Vehicle Registration Plate is required");
                }
                Vehicle vehicle = baseApplicationService.buildVehicle(vehicleDTO, register, branch, errors, language);

                application.getApplicationS7695Vehicles().add(
                        ApplicationS7695Vehicle.builder()
                                .ownershipType(nomenclatureService.findByCode(vehicleDTO.getVehicleOwnershipTypeCode()))
                                .applicationS7695(application)
                                .vehicle(vehicle).build()
                );
            }
        }

        if (dto.getAddress() != null) {
            Address address = AddressDTO.to(dto.getAddress());
            address.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE));
            address.setServiceType(SERVICE_TYPE);
            address.setSettlement(settlementService.findByCode(dto.getAddress().getSettlementCode()));
            application.setAddress(address);
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS7695(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordService.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final Set<KeyValueDTO> approvedFoodTypes, final Language language) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }
        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS7695 application = record.getApplicationS7695();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS7695.class, "null");
        }

        Facility facility = application.getFacility();
        if (facility == null) {
            throw new EntityNotFoundException(Facility.class, "null");
        }

        if (!CollectionUtils.isEmpty(approvedFoodTypes)) {
            approvedFoodTypes.forEach(ft -> application.getFoodTypes().add(classifierService.findByCode(ft.getCode())));
        }

        if (!CollectionUtils.isEmpty(application.getApplicationS7695Vehicles())) {
            for (ApplicationS7695Vehicle av : application.getApplicationS7695Vehicles()) {
                Vehicle vehicle = av.getVehicle();
                if (applicant.getContractorVehicles().stream()
                        .noneMatch(cv -> vehicle.getRegistrationPlate().equals(cv.getVehicle().getRegistrationPlate()))) {
                    vehicle.setEnabled(true);
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, av.getOwnershipType());
                }
            }
        }

        String activityDescription = facility.getI18n(language).getActivityDescription();

        if (StringUtils.hasText(application.getFacilityActivityDescription())) {
            activityDescription = activityDescription + "; " + application.getFacilityActivityDescription();
            facility.getI18n(language).setActivityDescription(activityDescription);
        }

        applicant.addContractorFacility(facility, SERVICE_TYPE,
                false, false,
                activityDescription, application.getCapacityUsage(),
                application.getUnitType(), application.getPeriodType());

        Contractor owner = contractorService.findByFacilityId(facility.getId());
        owner.addContractorRelation(applicant, SERVICE_TYPE, nomenclatureService.findByCode(Constants.TENANT));

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);
        return recordService.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS7695().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);
        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        mailService.sendApplicationCorrectionNotification(record, message, language);
        return recordRepository.save(record);
    }
}


