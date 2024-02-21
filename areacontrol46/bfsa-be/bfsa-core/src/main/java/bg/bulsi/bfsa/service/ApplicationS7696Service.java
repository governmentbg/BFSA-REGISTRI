package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS7696DTO;
import bg.bulsi.bfsa.dto.ApplicationS769DTO;
import bg.bulsi.bfsa.dto.ApproveFoodTypesDTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS7696;
import bg.bulsi.bfsa.model.ApplicationS7696Vehicle;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityI18n;
import bg.bulsi.bfsa.model.Language;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS7696Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordService recordService;
    private final NomenclatureService nomenclatureService;
    private final RecordRepository recordRepository;
    private final MailService mailService;
    private final VehicleService vehicleService;
    private final SettlementService settlementService;

    private final ServiceType SERVICE_TYPE = ServiceType.S7696;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002046_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS769DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS7696DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS7696DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        ApplicationS7696 application = ApplicationS7696.builder()
                .serviceRequestWrapper(wrapper)
                .commencementActivityDate(dto.getCommencementActivityDate())
                .status(ApplicationStatus.ENTERED)
                .build();


        // Check if the foodType classifier exists and add its parents
        if (!CollectionUtils.isEmpty(dto.getFacilities())) {
            for (FacilityDTO f : dto.getFacilities()) {
                if (!CollectionUtils.isEmpty(f.getFoodTypes())) {
                    Set<Classifier> foodTypesWithParent = new HashSet<>();
                    for (KeyValueDTO keyValue : f.getFoodTypes()) {
                        Classifier foodType = classifierService.findByCode(keyValue.getCode());
                        foodTypesWithParent.add(foodType);
                    }
                    f.setFoodTypes(KeyValueDTO.ofClassifiers(foodTypesWithParent, language));
                }
                f.setIdentifier(UUID.randomUUID().toString());
            }
            application.setApplicationS7696Facilities(dto.getFacilities());
        }

        // Използвам МПС за транспортиране на храни по чл. 50
        if (!CollectionUtils.isEmpty(dto.getCh50VehicleCertNumbers())) {
            for (String number : dto.getCh50VehicleCertNumbers()) {
                if (application.getApplicationS7696Vehicles().stream()
                        .noneMatch(v -> number.equals(v.getVehicle().getCertificateNumber()))) {
                    Vehicle vehicle = vehicleService.findByCertificateNumberOrNull(number, language);
                    if (vehicle != null) {
                        application.addApplicationS7696Vehicle(vehicle, null);
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

                application.getApplicationS7696Vehicles().add(
                        ApplicationS7696Vehicle.builder()
                                .ownershipType(nomenclatureService.findByCode(vehicleDTO.getVehicleOwnershipTypeCode()))
                                .applicationS7696(application)
                                .vehicle(vehicle).build()
                );
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS7696(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final List<ApproveFoodTypesDTO> approvedFoodTypes, final Language language) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        Classifier register = classifierService.findByCode(REGISTER_CODE);
        if (register == null) {
            throw new EntityNotFoundException(Classifier.class, "null");
        }

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        Branch branch = record.getBranch();
        if (branch == null) {
            throw new EntityNotFoundException(Branch.class, "null");
        }

        ApplicationS7696 application = record.getApplicationS7696();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS7696.class, "null");
        }

//        Данни за подвижни обекти и автомати за храна
        List<FacilityDTO> facilities = application.getApplicationS7696Facilities();
        if (!CollectionUtils.isEmpty(facilities)) {
            for (FacilityDTO dto : facilities) {
                if (!CollectionUtils.isEmpty(approvedFoodTypes)) {
                    approvedFoodTypes.stream()
                            .filter(f -> f.getIdentifier().equals(dto.getIdentifier()))
                            .findAny().ifPresent(facilityFoodType -> dto.setFoodTypes(facilityFoodType.getFoodTypes()));
                }
                Facility facility = buildFacility(dto, branch, register, language);

                String activityDescription = facility.getI18n(language).getActivityDescription();
                if (StringUtils.hasText(dto.getFoodTypeDescription())) {
                    activityDescription = activityDescription + "; " + dto.getFoodTypeDescription();
                    facility.getI18n(language).setActivityDescription(activityDescription);
                }

                facility.setRegNumber(baseApplicationService.generateNumber(record.getBranch().getSequenceNumber()));
                facility.setEnabled(true);
                facility.setStatus(FacilityStatus.ACTIVE);

                applicant.addContractorFacility(facility, SERVICE_TYPE, true, false,
                        dto.getActivityDescription(), null, null, null);

                application.getFacilities().add(facility);
            }
        }

//        МПС за транспортиране на храни по чл. 52
//        МПС за транспортиране на храни по чл. 50
        if (!CollectionUtils.isEmpty(application.getApplicationS7696Vehicles())) {
            for (ApplicationS7696Vehicle av : application.getApplicationS7696Vehicles()) {
                Vehicle vehicle = av.getVehicle();
                if (applicant.getContractorVehicles().stream()
                        .noneMatch(cv -> vehicle.getRegistrationPlate().equals(cv.getVehicle().getRegistrationPlate()))) {
                    vehicle.setEnabled(true);
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, av.getOwnershipType());
                }
            }
        }

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS7696().setStatus(ApplicationStatus.REJECTED);
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

    private Facility buildFacility(final FacilityDTO facilityDTO,
//                                   final ApplicationS7696 application,
                                   final Branch branch,
                                   final Classifier register,
                                   final Language language) {
        Facility facility = Facility.builder()
                .branch(branch)
//                .commencementActivityDate(application.getCommencementActivityDate())
                .status(FacilityStatus.ACTIVE)
                .waterSupplyType(StringUtils.hasText(facilityDTO.getWaterSupplyTypeCode())
                        ? nomenclatureService.findByCode(facilityDTO.getWaterSupplyTypeCode())
                        : null)
                .facilityType(StringUtils.hasText(facilityDTO.getFacilityTypeCode())
                        ? nomenclatureService.findByCode(facilityDTO.getFacilityTypeCode())
                        : null)
//                .activityType(StringUtils.hasText(facilityDTO.getActivityTypeCode()) ?
//                        nomenclatureService.findByCode(facilityDTO.getActivityTypeCode())
//                        : null)
                .capacity(facilityDTO.getCapacity())
                .disposalWasteWater(facilityDTO.getDisposalWasteWater())
                .area(facilityDTO.getArea())
                .enabled(true)
                .build();

        if (facilityDTO.getAddress() != null) {
            facility.setAddress(Address.builder()
                    .settlement(StringUtils.hasText(facilityDTO.getAddress().getSettlementCode())
                            ? settlementService.findByCode(facilityDTO.getAddress().getSettlementCode())
                            : null)
                    .address(facilityDTO.getAddress().getAddress())
                    .fullAddress(facilityDTO.getAddress().getFullAddress())
                    .postCode(facilityDTO.getAddress().getPostCode())
                    .phone(StringUtils.hasText(facilityDTO.getAddress().getPhone())
                            ? facilityDTO.getAddress().getPhone()
                            : null)
                    .addressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_PERMANENT_ADDRESS_CODE))
                    .mail(StringUtils.hasText(facilityDTO.getAddress().getMail())
                            ? facilityDTO.getAddress().getMail()
                            : null)
                    .enabled(true)
                    .build());
        }

        facility.getI18ns().add(new FacilityI18n(
                facilityDTO.getName(),
                facilityDTO.getDescription(),
                StringUtils.hasText(facilityDTO.getActivityDescription())
                        ? facilityDTO.getActivityDescription()
                        : null,
                StringUtils.hasText(facilityDTO.getPermission177())
                        ? facilityDTO.getPermission177()
                        : null,
                StringUtils.hasText(facilityDTO.getDisposalWasteWater())
                        ? facilityDTO.getDisposalWasteWater()
                        : null,
                StringUtils.hasText(facilityDTO.getFoodTypeDescription())
                        ? facilityDTO.getFoodTypeDescription()
                        : null,
                facility, language));

        if (!CollectionUtils.isEmpty(facilityDTO.getFoodTypes())) {
            facilityDTO.getFoodTypes().forEach(tc -> facility.getFoodTypes().add(classifierService.findByCode(tc.getCode())));
        }

        if (register != null) {
            if (facility.getRegisters().stream().noneMatch(c -> register.getCode().equals(c.getCode()))) {
                facility.getRegisters().add(register);
            }
        }

        return facility;
    }
}
