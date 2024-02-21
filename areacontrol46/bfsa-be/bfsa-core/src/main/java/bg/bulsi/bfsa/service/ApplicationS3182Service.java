package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS3182DTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS3182;
import bg.bulsi.bfsa.model.ApplicationS3182Vehicle;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityCapacity;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS3182Service {
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final SettlementService settlementService;
    private final CountryService countryService;
    private final NomenclatureService nomenclatureService;
    private final BranchService branchService;
    private final MailService mailService;
    private final BaseApplicationService baseApplicationService;
    private final ClassifierService classifierService;
    private final ServiceType SERVICE_TYPE = ServiceType.S3182;

    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002016_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS3182DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS3182DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS3182DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(REGISTER_CODE);
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

        ApplicationS3182 application = ApplicationS3182.builder()
                .facility(facility)
                .serviceRequestWrapper(wrapper)
                .commencementActivityDate(dto.getCommencementActivityDate())
                .status(ApplicationStatus.ENTERED).build();

        if (dto.getAddress() != null) {
            application.setAddress(Address.builder()
                    .url(dto.getAddress().getUrl())
                    .mail(dto.getAddress().getMail())
                    .phone(dto.getAddress().getPhone())
                    .address(dto.getAddress().getAddress())
                    .fullAddress(dto.getAddress().getFullAddress())
                    .addressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE))
                    .country(StringUtils.hasText(dto.getAddress().getCountryCode()) ? countryService.findByCode(dto.getAddress().getCountryCode()) : null)
                    .settlement(StringUtils.hasText(dto.getAddress().getSettlementCode()) ? settlementService.findByCode(dto.getAddress().getSettlementCode()) : null)
                    .build()
            );
        }

        // Използвам МПС за транспортиране на храни по чл. 52
        if (!CollectionUtils.isEmpty(dto.getVehicles())) {
            for (VehicleDTO vehicleDTO : dto.getVehicles()) {
                if (!StringUtils.hasText(vehicleDTO.getRegistrationPlate())) {
                    throw new RuntimeException("Vehicle Registration Plate is required");
                }
                Vehicle vehicle = baseApplicationService.buildVehicle(vehicleDTO, register, branch, errors, language);

                application.getApplicationS3182Vehicles().add(
                        ApplicationS3182Vehicle.builder()
                                .ownerType(nomenclatureService.findByCode(vehicleDTO.getVehicleOwnershipTypeCode()))
                                .applicationS3182(application)
                                .vehicle(vehicle).build()
                );
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS3182(application);
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

        Facility facility = record.getFacility();
        if (facility == null) {
            throw new EntityNotFoundException(Facility.class, "null");
        }

        ApplicationS3182 application = record.getApplicationS3182();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3182.class, "null");
        }

        // Add distance trading address
        if (application.getAddress() != null) {
            Address address = application.getAddress();
            address.setServiceType(SERVICE_TYPE);
            address.setAddressType(nomenclatureService.findByCode(
                    Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE)
            );
            applicant.getAddresses().add(address);
        }

        if (applicant.getContractorFacilities().stream().noneMatch(cf -> facility.getId().equals(cf.getFacility().getId())
                && SERVICE_TYPE.equals(cf.getServiceType()))) {
            applicant.addContractorFacility(facility, SERVICE_TYPE, true);
        }

        if (!CollectionUtils.isEmpty(application.getApplicationS3182Vehicles())) {
            for (ApplicationS3182Vehicle av : application.getApplicationS3182Vehicles()) {
                Vehicle vehicle = av.getVehicle();
                if (applicant.getContractorVehicles().stream()
                        .noneMatch(cv -> vehicle.getRegistrationPlate().equals(cv.getVehicle().getRegistrationPlate()))) {
                    vehicle.setEnabled(true);
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, av.getOwnerType());
                }
            }
        }

        facility.setRegNumber(baseApplicationService.generateNumber(facility.getBranch().getSequenceNumber()));
        facility.setRegDate(LocalDate.now());

        facility.setStatus(FacilityStatus.ACTIVE);
        record.getApplicationS3182().setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS3182().setStatus(ApplicationStatus.REJECTED);
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

    private Facility buildFacility(final ApplicationS3182DTO dto, final Branch branch, final Classifier register, final Language language) {
        String fullAddress = dto.getFacility().getAddress().getFullAddress();
        if (!StringUtils.hasText(fullAddress)) {
            fullAddress = (StringUtils.hasText(dto.getFacility().getAddress().getAddress())
                    ? dto.getFacility().getAddress().getAddress() + ", " : "") +
                    settlementService.getInfo(dto.getFacility().getAddress().getSettlementCode(), language.getLanguageId());
        }

        Facility facility = Facility.builder()
                .branch(branch)
                .address(
                        Address.builder()
                                .settlement(StringUtils.hasText(dto.getFacility().getAddress().getSettlementCode())
                                        ? settlementService.findByCode(dto.getFacility().getAddress().getSettlementCode())
                                        : null)
                                .address(dto.getFacility().getAddress().getAddress())
                                .fullAddress(StringUtils.hasText(fullAddress) ? fullAddress : null)
                                .postCode(dto.getFacility().getAddress().getPostCode())
                                .phone(StringUtils.hasText(dto.getFacility().getAddress().getPhone())
                                        ? dto.getFacility().getAddress().getPhone()
                                        : null)
                                .addressType(null)// TODO Add nomenclature (00202..3..4) for facility address type
                                .mail(StringUtils.hasText(dto.getFacility().getAddress().getMail())
                                        ? dto.getFacility().getAddress().getMail()
                                        : null)
                                .enabled(true) // TODO: Check
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
                null
                , facility, language));

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

        if (register != null) {
            if (facility.getRegisters().stream().noneMatch(c -> register.getCode().equals(c.getCode()))) {
                facility.getRegisters().add(register);
            }
        }

        return facility;
    }
}