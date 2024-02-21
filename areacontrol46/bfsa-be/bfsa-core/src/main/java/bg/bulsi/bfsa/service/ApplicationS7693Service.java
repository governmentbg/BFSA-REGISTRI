package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS7693DTO;
import bg.bulsi.bfsa.dto.ApplicationS769DTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS7693;
import bg.bulsi.bfsa.model.ApplicationS7693Vehicle;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
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

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS7693Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordService recordService;
    private final VehicleService vehicleService;
    private final NomenclatureService nomenclatureService;
    private final RecordRepository recordRepository;
    private final MailService mailService;
    private final CountryService countryService;
    private final SettlementService settlementService;

    private final ServiceType SERVICE_TYPE = ServiceType.S7693;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;
    public static String REGISTER_CODE;

    @Transactional(readOnly = true)
    public ApplicationS769DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS7693DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS7693DTO dto, final ServiceRequestWrapper wrapper, final Language language) {

        if (dto.getFacility() != null) {
            String activity = dto.getFacility().getActivityTypeCode();
            switch (activity) {
                case Constants.ACTIVITY_TYPE_WHOLESALE_CODE,
                        Constants.ACTIVITY_TYPE_NON_ANIMAL_FOOD_CODE:
                    REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002008_CODE;
                    break;
                case Constants.ACTIVITY_OUTSIDE_REGULATION_853_CODE:
                    REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002006_CODE;
                    break;
                default:
                    throw new RuntimeException("Unknown activity type " + activity);
            }
        }

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

        ApplicationS7693 application = ApplicationS7693.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();

        application.setFoodTypeDescription(dto.getFoodTypeDescription());
        application.setCommencementActivityDate(dto.getCommencementActivityDate());

        // Check if the foodType classifier exists and add its parents
        if (!CollectionUtils.isEmpty(dto.getFoodTypes())) {
            Set<Classifier> foodTypesWithParent = new HashSet<>();
            for (KeyValueDTO keyValue : dto.getFoodTypes()) {
                Classifier foodType = classifierService.findByCode(keyValue.getCode());
                foodTypesWithParent.add(foodType);
            }
            application.setApplicationS7693FoodTypes(KeyValueDTO.ofClassifiers(foodTypesWithParent, language));
        }

        // Използвам МПС за транспортиране на храни по чл. 50
        if (!CollectionUtils.isEmpty(dto.getCh50VehicleCertNumbers())) {
            for (String number : dto.getCh50VehicleCertNumbers()) {
                if (application.getApplicationS7693Vehicles().stream().noneMatch(v -> number.equals(v.getVehicle().getCertificateNumber()))) {
                    Vehicle vehicle = vehicleService.findByCertificateNumberOrNull(number, language);
                    if (vehicle != null) {
                        application.addApplicationS7693Vehicle(vehicle, null);
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

                application.getApplicationS7693Vehicles().add(
                        ApplicationS7693Vehicle.builder()
                                .ownershipType(nomenclatureService.findByCode(vehicleDTO.getVehicleOwnershipTypeCode()))
                                .applicationS7693(application)
                                .vehicle(vehicle).build()
                );
            }
        }

        if (dto.getFacility() != null) {
            Facility facility = FacilityDTO.to(dto.getFacility(), language);
            facility.setFacilityType(StringUtils.hasText(dto.getFacility().getFacilityTypeCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getFacilityTypeCode())
                    : null);
            facility.setActivityType(StringUtils.hasText(dto.getFacility().getActivityTypeCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getActivityTypeCode())
                    : null);
            facility.setSubActivityType(StringUtils.hasText(dto.getFacility().getSubActivityTypeCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getSubActivityTypeCode())
                    : null);
            facility.setWaterSupplyType(StringUtils.hasText(dto.getFacility().getWaterSupplyTypeCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getWaterSupplyTypeCode())
                    : null);
            facility.setMeasuringUnit(StringUtils.hasText(dto.getFacility().getMeasuringUnitCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getMeasuringUnitCode())
                    : null);
            facility.setPeriod(StringUtils.hasText(dto.getFacility().getPeriodCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getPeriodCode())
                    : null);

            Address facilityAddress = AddressDTO.to(dto.getFacility().getAddress());
            facilityAddress.setAddressType(StringUtils.hasText(dto.getFacility().getAddress().getAddressTypeCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getAddress().getAddressTypeCode())
                    : null);
            facilityAddress.setSettlement(StringUtils.hasText(dto.getFacility().getAddress().getSettlementCode())
                    ? settlementService.findByCode(dto.getFacility().getAddress().getSettlementCode())
                    : null);

            facility.setAddress(facilityAddress);
            application.setFacility(facility);
        }

        if (dto.getAddress() != null) {
            application.setAddress(Address.builder()
                    .url(dto.getAddress().getUrl())
                    .mail(dto.getAddress().getMail())
                    .phone(dto.getAddress().getPhone())
                    .address(dto.getAddress().getAddress())
                    .fullAddress(dto.getAddress().getFullAddress())
                    .addressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE))
                    .country(StringUtils.hasText(dto.getAddress().getCountryCode())
                            ? countryService.findByCode(dto.getAddress().getCountryCode())
                            : null)
                    .settlement(StringUtils.hasText(dto.getAddress().getSettlementCode())
                            ? settlementService.findByCode(dto.getAddress().getSettlementCode())
                            : null)
                            .serviceType(SERVICE_TYPE)
                    .build()
            );
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS7693(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordService.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final Set<KeyValueDTO> approvedFoodTypes) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }
        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS7693 application = record.getApplicationS7693();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS7693.class, "null");
        }

        if (!CollectionUtils.isEmpty(approvedFoodTypes)) {
            approvedFoodTypes.forEach(ft -> application.getFoodTypes().add(classifierService.findByCode(ft.getCode())));
        }

        if (!CollectionUtils.isEmpty(application.getApplicationS7693Vehicles())) {
            for (ApplicationS7693Vehicle av : application.getApplicationS7693Vehicles()) {
                Vehicle vehicle = av.getVehicle();
                if (applicant.getContractorVehicles().stream()
                        .noneMatch(cv -> vehicle.getRegistrationPlate().equals(cv.getVehicle().getRegistrationPlate()))) {
                    vehicle.setEnabled(true);
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, av.getOwnershipType());
                }
            }
        }

        if (application.getAddress() != null) {
            Address address = application.getAddress();
            address.setServiceType(SERVICE_TYPE);
            address.setAddressType(nomenclatureService.findByCode(
                    Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE));
            applicant.getAddresses().add(address);
        }

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);
        return recordService.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS7693().setStatus(ApplicationStatus.REJECTED);
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


