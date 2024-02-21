package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS3180DTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.enums.VehicleStatus;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS3180;
import bg.bulsi.bfsa.model.ApplicationS3180Vehicle;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityCapacity;
import bg.bulsi.bfsa.model.FacilityI18n;
import bg.bulsi.bfsa.model.FacilityPaper;
import bg.bulsi.bfsa.model.FacilityPaperI18n;
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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS3180Service {
    private final RecordRepository repository;
    private final RecordService recordService;
    private final SettlementService settlementService;
    private final NomenclatureService nomenclatureService;
    private final ActivityGroupService activityGroupService;
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final VehicleService vehicleService;
    private final BaseApplicationService baseApplicationService;
    private final CountryService countryService;

    private final ServiceType SERVICE_TYPE = ServiceType.S3180;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS3180DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS3180DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS3180DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(
                Constants.ACTIVITY_TYPE_MILK_COLLECTION_CENTER_CODE.equals(dto.getFacility().getActivityTypeCode())
                        ? Constants.CLASSIFIER_REGISTER_0002005_CODE
                        : Constants.CLASSIFIER_REGISTER_0002004_CODE
        );
        List<String> errors = new ArrayList<>();

        Contractor applicant = baseApplicationService.buildApplicant(
                dto.getApplicantIdentifier(), dto.getEntityType(),
                dto.getApplicantFullName(), dto.getApplicantEmail(),
                dto.getApplicantPhone(), branch,
                dto.getApplicantCorrespondenceAddress(), null, errors, SERVICE_TYPE, language);

        Contractor requestor = applicant;

        if (!Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_CODE.equals(dto.getRequestorAuthorTypeCode())
                && !Constants.REQUESTOR_AUTHOR_TYPE_HOLDER_EXTERNAL_CODE.equals(dto.getRequestorAuthorTypeExternalCode())
                && !dto.getApplicantIdentifier().equals(dto.getRequestorIdentifier())) {
            requestor = baseApplicationService.buildRequestor(
                    dto.getRequestorIdentifier(), dto.getRequestorFullName(),
                    dto.getRequestorEmail(), dto.getRequestorPhone(),
                    dto.getRequestorCorrespondenceAddress(), errors, SERVICE_TYPE, language);
        }

        Facility facility = buildFacility(dto, branch, register, language);

        ApplicationS3180 application = ApplicationS3180.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED)
                .commencementActivityDate(dto.getCommencementActivityDate())
                .facility(facility).build();

        if (dto.getAddress() != null) {
            Address address = Address.builder()
                    .url(dto.getAddress().getUrl())
                    .mail(dto.getAddress().getMail())
                    .phone(dto.getAddress().getPhone())
                    .address(dto.getAddress().getAddress())
                    .fullAddress(dto.getAddress().getFullAddress())
                    .serviceType(SERVICE_TYPE)
                    .build();
            if (StringUtils.hasText(dto.getAddress().getSettlementCode())) {
                address.setSettlement(settlementService.findByCode(dto.getAddress().getSettlementCode()));
                address.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE));
            } else if (StringUtils.hasText(dto.getAddress().getCountryCode())) {
                Country country = countryService.findByCode(dto.getAddress().getCountryCode());
                address.setCountry(country);
                address.setFullAddress(country.getI18n(language).getName() + ", " + address.getAddress());
                address.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_FOREIGN_FACILITY_CODE));
            }
            application.setAddress(address);
        }

        // Използвам МПС за транспортиране на храни по чл. 50
        if (!CollectionUtils.isEmpty(dto.getCh50VehicleCertNumbers())) {
            for (String number : dto.getCh50VehicleCertNumbers()) {
                if (application.getApplicationS3180Vehicles().stream().noneMatch(v -> number.equals(v.getVehicle().getCertificateNumber()))) {
                    Vehicle vehicle = vehicleService.findByCertificateNumberOrNull(number, language);
                    if (vehicle != null) {
                        application.addApplicationS3180Vehicle(vehicle, null);
                    } else {
                        errors.add("Vehicle with certificate number: " + number + " doesn't exist.");
                    }
                }
            }
        }

//        // Използвам МПС за транспортиране на храни по чл. 50
//        if (!CollectionUtils.isEmpty(dto.getCh50VehicleCertNumbers())) {
//            for (String number : dto.getCh50VehicleCertNumbers()) {
//                boolean applicationVehicleNoneMatch = application.getApplicationS3180Vehicles().stream()
//                        .map(ApplicationS3180Vehicle::getVehicle).noneMatch(v -> number.equals(v.getCertificateNumber()));
//                boolean facilityVehicleNoneMatch = facility.getVehicles().stream().noneMatch(v -> number.equals(v.getCertificateNumber()));
//                if (applicationVehicleNoneMatch || facilityVehicleNoneMatch) {
//                    Vehicle vehicle = vehicleService.findByCertificateNumberOrNull(number, language);
//                    if (vehicle != null) {
//                        if (applicationVehicleNoneMatch) {
//                            application.getVehicles().add(vehicle);
//                        }
//                        if (facilityVehicleNoneMatch) {
//                            facility.getVehicles().add(vehicle);
//                        }
//                    } else {
//                        errors.add("Vehicle with certificate number: " + number + " doesn't exist.");
//                    }
//                }
//            }
//        }

        // Използвам МПС за транспортиране на храни по чл. 52
        if (!CollectionUtils.isEmpty(dto.getVehicles())) {
            for (VehicleDTO vehicleDTO : dto.getVehicles()) {
                if (!StringUtils.hasText(vehicleDTO.getRegistrationPlate())) {
                    throw new RuntimeException("Vehicle Registration Plate is required");
                }
                Vehicle vehicle = baseApplicationService.buildVehicle(vehicleDTO, register, branch, errors, language);

                application.getApplicationS3180Vehicles().add(
                        ApplicationS3180Vehicle.builder()
                                .ownerType(nomenclatureService.findByCode(vehicleDTO.getVehicleOwnershipTypeCode()))
                                .applicationS3180(application)
                                .vehicle(vehicle).build()
                );
//                facility.getVehicles().add(vehicle); // TODO: Да се прикачат ли МПС-та към Обекта тук или в approve()?
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS3180(application);
        record.setFacility(facility);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return repository.save(record);
    }

    @Transactional
    public Record activityDescription(final Long recordId,
                                      final ApplicationS3180DTO dto,
                                      final Language language) {
        if (recordId == null || recordId <= 0) {
            throw new RuntimeException("recordId field is required");
        }
        if (!recordId.equals(dto.getRecordId())) {
            throw new RuntimeException("Path variable recordId doesn't match RequestBody parameter recordId");
        }

        Record record = recordService.findById(recordId);
        Facility facility = record.getFacility();

        facility.getI18n(language).setDescription(dto.getFacility().getDescription());

        if (dto.getActivityGroupId() != null && dto.getActivityGroupId() > 0) {
            facility.setActivityGroup(activityGroupService.findById(dto.getActivityGroupId()));
        }

        facility.getRelatedActivityCategories().clear();
        if (!CollectionUtils.isEmpty(dto.getRelatedActivityCategories())) {
            for (String code : dto.getRelatedActivityCategories()) {
                facility.getRelatedActivityCategories().add(nomenclatureService.findByCode(code));
            }
        }
        facility.getAssociatedActivityCategories().clear();
        if (!CollectionUtils.isEmpty(dto.getAssociatedActivityCategories())) {
            for (String code : dto.getAssociatedActivityCategories()) {
                facility.getAssociatedActivityCategories().add(nomenclatureService.findByCode(code));
            }
        }
        facility.getAnimalSpecies().clear();
        if (!CollectionUtils.isEmpty(dto.getAnimalSpecies())) {
            for (String code : dto.getAnimalSpecies()) {
                facility.getAnimalSpecies().add(nomenclatureService.findByCode(code));
            }
        }
        facility.getRemarks().clear();
        if (!CollectionUtils.isEmpty(dto.getRemarks())) {
            for (String code : dto.getRemarks()) {
                facility.getRemarks().add(nomenclatureService.findByCode(code));
            }
        }
        facility.getPictograms().clear();
        if (!CollectionUtils.isEmpty(dto.getPictograms())) {
            for (String code : dto.getPictograms()) {
                facility.getPictograms().add(nomenclatureService.findByCode(code));
            }
        }
        facility.getFoodTypes().clear();
        if (!CollectionUtils.isEmpty(dto.getFoodTypes())) {
            for (KeyValueDTO ft : dto.getFoodTypes()) {
                facility.getFoodTypes().add(classifierService.findByCode(ft.getCode()));
            }
        }
        // Set to status PROCESSING when expert touched application
        if (RecordStatus.PAYMENT_CONFIRMED.equals(record.getStatus()) ||
                RecordStatus.ENTERED.equals(record.getStatus())) {
            record.setStatus(RecordStatus.PROCESSING);
        }

        return repository.save(record);
    }

    @Transactional
    public Record directRegistration(final Long recordId, Language language) {

        Record record = recordService.findById(recordId);

        Facility facility = record.getFacility();
        if (facility == null) {
            throw new EntityNotFoundException(Facility.class, "null");
        }

        FacilityPaper paper = FacilityPaper.builder()
                .facility(facility)
                .serviceType(SERVICE_TYPE)
                .record(record)
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .build();
        paper.getI18ns().add(
                new FacilityPaperI18n(ServiceType.S3180.name(),
                        "ЗАЯВЛЕНИЕ за одобрение на обекти за производство и търговия на едро с храни от животински произход",
                        paper,
                        language)
        );

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());

        facility.getFacilityPapers().add(paper);
        record.setFacilityPaper(paper);

        record.setStatus(RecordStatus.FINAL_APPROVED);

        return repository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS3180().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return repository.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final Language language) {
        Record record = recordService.findById(recordId);

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        Facility facility = record.getFacility();
        if (facility == null) {
            throw new EntityNotFoundException(Facility.class, "null");
        }

        ApplicationS3180 application = record.getApplicationS3180();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3180.class, "null");
        }

        // Add distance trading address
        if (application.getAddress() != null) {
            Address address = application.getAddress();
            address.setServiceType(SERVICE_TYPE);
            address.setAddressType(nomenclatureService.findByCode(
                    Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE)
            );
            applicant.getAddresses().add(application.getAddress());
        }

        if (applicant.getContractorFacilities().stream().noneMatch(cf -> facility.getId().equals(cf.getFacility().getId())
                && SERVICE_TYPE.equals(cf.getServiceType()))) {
            applicant.addContractorFacility(facility, SERVICE_TYPE, true);
        }

        if (!CollectionUtils.isEmpty(application.getApplicationS3180Vehicles())) {
            for (ApplicationS3180Vehicle apV : application.getApplicationS3180Vehicles()) {
                Vehicle vehicle = apV.getVehicle();
                if (applicant.getContractorVehicles().stream()
                        .noneMatch(cv -> vehicle.getRegistrationPlate().equals(cv.getVehicle().getRegistrationPlate()))) {
                    vehicle.setEnabled(true);
                    // Set vehicle status only if it is null
                    if (vehicle.getStatus() == null) {
                        vehicle.setStatus(VehicleStatus.ACTIVE);
                    }
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, apV.getOwnerType());
                }
                if (facility.getVehicles().stream().noneMatch(v -> v.getId().equals(vehicle.getId()))) {
                    facility.getVehicles().add(vehicle);
                }
            }
        }

        FacilityPaper paper = FacilityPaper.builder()
                .facility(facility)
                .serviceType(SERVICE_TYPE)
                .record(record)
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .build();
        paper.getI18ns().add(
                new FacilityPaperI18n(ServiceType.S3180.name(),
                        "ЗАЯВЛЕНИЕ за одобрение на обекти за производство и търговия на едро с храни от животински произход",
                        paper,
                        language)
        );

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());

        facility.getFacilityPapers().add(paper);
        record.setFacilityPaper(paper);

        facility.setStatus(FacilityStatus.ACTIVE);
        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return repository.save(record);
    }

    private Facility buildFacility(final ApplicationS3180DTO dto, Branch branch, final Classifier register, final Language language) {
        Facility facility = Facility.builder()
                .branch(branch)
                .address(
                        Address.builder()
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
                                .build()
                )
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
                .period(StringUtils.hasText(dto.getFacility().getPeriodCode())
                        ? nomenclatureService.findByCode(dto.getFacility().getPeriodCode())
                        : null)
                .subActivityType(StringUtils.hasText(dto.getFacility().getSubActivityTypeCode())
                        ? nomenclatureService.findByCode(dto.getFacility().getSubActivityTypeCode())
                        : null)
                .activityType(StringUtils.hasText(dto.getFacility().getActivityTypeCode()) ?
                        nomenclatureService.findByCode(dto.getFacility().getActivityTypeCode())
                        : null)
                .capacity(dto.getFacility().getCapacity())
                .disposalWasteWater(dto.getFacility().getDisposalWasteWater())
                .enabled(true)
                .build();

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
                null,
                facility, language));

        if (dto.getFacility().getFacilityCapacities() != null) {
            dto.getFacility().getFacilityCapacities().forEach(fc -> facility.getFacilityCapacities()
                    .add(FacilityCapacity.builder()
                            .product(fc.getProduct())
                            .quantity(fc.getQuantity())
                            .material(StringUtils.hasText(fc.getMaterialCode())
                                    ? nomenclatureService.findByCode(fc.getMaterialCode())
                                    : null)
                            .unit(StringUtils.hasText(fc.getUnitCode())
                                    ? nomenclatureService.findByCode(fc.getUnitCode())
                                    : null)
                            .facility(facility)
                            .fridgeCapacity(fc.getFridgeCapacity())
                            .rawMilkType(StringUtils.hasText(fc.getRawMilkTypeCode())
                                    ? nomenclatureService.findByCode(fc.getRawMilkTypeCode())
                                    : null)
                            .build()));
        }

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