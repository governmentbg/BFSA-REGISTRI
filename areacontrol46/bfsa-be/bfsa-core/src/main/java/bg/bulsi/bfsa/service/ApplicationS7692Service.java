package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS7692DTO;
import bg.bulsi.bfsa.dto.ApplicationS769DTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS7692;
import bg.bulsi.bfsa.model.ApplicationS7692Vehicle;
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

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS7692Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordService recordService;
    private final VehicleService vehicleService;
    private final NomenclatureService nomenclatureService;
    private final RecordRepository recordRepository;
    private final MailService mailService;
    private final SettlementService settlementService;

    private final ServiceType SERVICE_TYPE = ServiceType.S7692;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS769DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS7692DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS7692DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(
                Constants.ACTIVITY_PUBLIC_CATERING_CODE.equals(dto.getFacility().getActivityTypeCode())
                        ? Constants.CLASSIFIER_REGISTER_0002011_CODE
                        : Constants.CLASSIFIER_REGISTER_0002010_CODE
        );

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

        ApplicationS7692 application = ApplicationS7692.builder()
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
            application.setApplicationS7692FoodTypes(KeyValueDTO.ofClassifiers(foodTypesWithParent, language));
        }

        // Използвам МПС за транспортиране на храни по чл. 50
        if (!CollectionUtils.isEmpty(dto.getCh50VehicleCertNumbers())) {
            for (String number : dto.getCh50VehicleCertNumbers()) {
                if (application.getApplicationS7692Vehicles().stream().noneMatch(v -> number.equals(v.getVehicle().getCertificateNumber()))) {
                    Vehicle vehicle = vehicleService.findByCertificateNumberOrNull(number, language);
                    if (vehicle != null) {
                        application.addApplicationS7692Vehicle(vehicle, null);
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

                application.getApplicationS7692Vehicles().add(
                        ApplicationS7692Vehicle.builder()
                                .ownershipType(nomenclatureService.findByCode(vehicleDTO.getVehicleOwnershipTypeCode()))
                                .applicationS7692(application)
                                .vehicle(vehicle).build()
                );
            }
        }

        if (dto.getAddress() != null) {
            Address address = AddressDTO.to(dto.getAddress());
            address.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE));
            address.setServiceType(SERVICE_TYPE);
            address.setSettlement(settlementService.findByCode(dto.getAddress().getSettlementCode()));
            application.setRemoteTradingAddress(address);
        }

        Facility facility = buildFacility(dto, branch, register, language);
        application.setFacility(facility);

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS7692(application);
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

        ApplicationS7692 application = record.getApplicationS7692();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS7692.class, "null");
        }

        Facility facility = record.getApplicationS7692().getFacility();
        if (facility == null) {
            throw new EntityNotFoundException(Facility.class, "null");
        }

        if (!CollectionUtils.isEmpty(approvedFoodTypes)) {
            approvedFoodTypes.forEach(ft -> application.getFoodTypes().add(classifierService.findByCode(ft.getCode())));
        }

        if (!CollectionUtils.isEmpty(application.getApplicationS7692Vehicles())) {
            for (ApplicationS7692Vehicle av : application.getApplicationS7692Vehicles()) {
                Vehicle vehicle = av.getVehicle();
                if (applicant.getContractorVehicles().stream()
                        .noneMatch(cv -> vehicle.getRegistrationPlate().equals(cv.getVehicle().getRegistrationPlate()))) {
                    vehicle.setEnabled(true);
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, av.getOwnershipType());
                }
            }
        }

        String activityDescription = facility.getI18n(language).getActivityDescription();

        if (StringUtils.hasText(application.getFoodTypeDescription())) {
            activityDescription = activityDescription + "; " + application.getFoodTypeDescription();
            facility.getI18n(language).setActivityDescription(activityDescription);
        }

        applicant.addContractorFacility(facility, SERVICE_TYPE,
                true, false,
                activityDescription, null,
                null, null);

//        TODO: Check line below: Should be added?
        facility.setRegNumber(baseApplicationService.generateNumber(record.getBranch().getSequenceNumber()));
        facility.setStatus(FacilityStatus.ACTIVE);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordService.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS7692().setStatus(ApplicationStatus.REJECTED);
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

    private Facility buildFacility(final ApplicationS7692DTO dto,
                                   final Branch branch,
                                   final Classifier register,
                                   final Language language) {
        Facility facility = Facility.builder()
                .branch(branch)
                .commencementActivityDate(dto.getCommencementActivityDate())
                .status(FacilityStatus.INACTIVE)
                .waterSupplyType(StringUtils.hasText(dto.getFacility().getWaterSupplyTypeCode())
                        ? nomenclatureService.findByCode(dto.getFacility().getWaterSupplyTypeCode())
                        : null)
                .facilityType(StringUtils.hasText(dto.getFacility().getFacilityTypeCode())
                        ? nomenclatureService.findByCode(dto.getFacility().getFacilityTypeCode())
                        : null)
                .measuringUnit(StringUtils.hasText(dto.getFacility().getMeasuringUnitCode())
                        ? nomenclatureService.findByCode(dto.getFacility().getMeasuringUnitCode())
                        : null)
                .activityType(StringUtils.hasText(dto.getFacility().getActivityTypeCode()) ?
                        nomenclatureService.findByCode(dto.getFacility().getActivityTypeCode())
                        : null)
                .capacity(dto.getFacility().getCapacity())
                .disposalWasteWater(dto.getFacility().getDisposalWasteWater())
                .area(dto.getFacility().getArea())
                .enabled(true)
                .build();

        if (dto.getFacility().getAddress() != null) {
            facility.setAddress(Address.builder()
                    .settlement(StringUtils.hasText(dto.getFacility().getAddress().getSettlementCode())
                            ? settlementService.findByCode(dto.getFacility().getAddress().getSettlementCode())
                            : null)
                    .address(dto.getFacility().getAddress().getAddress())
                    .fullAddress(dto.getFacility().getAddress().getFullAddress())
                    .postCode(dto.getFacility().getAddress().getPostCode())
                    .phone(StringUtils.hasText(dto.getFacility().getAddress().getPhone())
                            ? dto.getFacility().getAddress().getPhone()
                            : null)
                    .addressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_PERMANENT_ADDRESS_CODE))
                    .mail(StringUtils.hasText(dto.getFacility().getAddress().getMail())
                            ? dto.getFacility().getAddress().getMail()
                            : null)
                    .enabled(true)
                    .build());
        }

        facility.getI18ns().add(new FacilityI18n(
                dto.getFacility().getName(),
                dto.getFacility().getDescription(),
                StringUtils.hasText(dto.getFacility().getActivityDescription())
                        ? dto.getFacility().getActivityDescription()
                        : null,
                StringUtils.hasText(dto.getFacility().getPermission177())
                        ? dto.getFacility().getPermission177()
                        : null,
                StringUtils.hasText(dto.getFacility().getDisposalWasteWater())
                        ? dto.getFacility().getDisposalWasteWater()
                        : null,
                StringUtils.hasText(dto.getFacility().getFoodTypeDescription())
                        ? dto.getFacility().getFoodTypeDescription()
                        : null,
                facility, language));

        if (!CollectionUtils.isEmpty(dto.getFacility().getFoodTypes())) {
            dto.getFacility().getFoodTypes().forEach(tc -> facility.getFoodTypes().add(classifierService.findByCode(tc.getCode())));
        }

        if (register != null) {
            if (facility.getRegisters().stream().noneMatch(c -> register.getCode().equals(c.getCode()))) {
                facility.getRegisters().add(register);
            }
        }

        return facility;
    }
}


