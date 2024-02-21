package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS3181DTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS3181;
import bg.bulsi.bfsa.model.ApplicationS3181Vehicle;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS3181Service {
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final SettlementService settlementService;
    private final NomenclatureService nomenclatureService;
    private final BranchService branchService;
    private final FacilityService facilityService;
    private final CountryService countryService;
    private final VehicleService vehicleService;
    private final MailService mailService;
    private final BaseApplicationService baseApplicationService;
    private final ClassifierService classifierService;

    private final ServiceType SERVICE_TYPE = ServiceType.S3181;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002019_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS3181DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS3181DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS3181DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        ApplicationS3181 application = ApplicationS3181.builder()
                .serviceRequestWrapper(wrapper)
                .commencementActivityDate(dto.getCommencementActivityDate())
                .status(ApplicationStatus.ENTERED).build();

        if (dto.getAddress() != null) {
            application.getAddresses().add(Address.builder()
                    .url(dto.getAddress().getUrl())
                    .mail(dto.getAddress().getMail())
                    .phone(dto.getAddress().getPhone())
                    .address(dto.getAddress().getAddress())
                    .fullAddress(dto.getAddress().getFullAddress())
                    .addressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE))
                    .settlement(settlementService.findByCode(dto.getAddress().getSettlementCode()))
                    .serviceType(SERVICE_TYPE)
                    .build()
            );
        }

        if (!CollectionUtils.isEmpty(dto.getForeignFacilityAddresses())) {
            dto.getForeignFacilityAddresses()
                    .forEach(a -> application.getAddresses().add(
                            Address.builder()
                                    .address(a.getAddress())
                                    .addressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_FOREIGN_FACILITY_CODE))
                                    .country(countryService.findByCode(a.getCountryCode()))
                                    .serviceType(SERVICE_TYPE)
                                    .build()));
        }

        if (!CollectionUtils.isEmpty(dto.getFacilitiesPaperNumbers())) {
            for (String number : dto.getFacilitiesPaperNumbers()) {
                Facility facility = facilityService.findByRegNumberOrNull(number);
                if (facility != null) {
                    application.getFacilities().add(facility);
                } else {
                    errors.add("Facility with certificate number: " + number + " doesn't exist.");
                }
            }
        }

        // Използвам МПС за транспортиране на храни по чл. 50
        if (!CollectionUtils.isEmpty(dto.getCh50VehicleCertNumbers())) {
            for (String number : dto.getCh50VehicleCertNumbers()) {
                if (application.getApplicationS3181Vehicles().stream().noneMatch(v -> number.equals(v.getVehicle().getCertificateNumber()))) {
                    Vehicle vehicle = vehicleService.findByCertificateNumberOrNull(number, language);
                    if (vehicle != null) {
                        application.addApplicationS3181Vehicle(vehicle, null);
                    } else {
                        errors.add("Vehicle with certificate number: " + number + " doesn't exist.");
                    }
                }
            }
        }

        // Използвам МПС за транспортиране на храни по чл. 52
        if (!CollectionUtils.isEmpty(dto.getVehicles())) {
            for (VehicleDTO vehicleDTO : dto.getVehicles()) {
                Vehicle vehicle = baseApplicationService.buildVehicle(vehicleDTO, register, branch, errors, language);

                application.getApplicationS3181Vehicles().add(
                        ApplicationS3181Vehicle.builder()
                        .ownerType(nomenclatureService.findByCode(vehicleDTO.getVehicleOwnershipTypeCode()))
                        .applicationS3181(application)
                        .vehicle(vehicle).build()
                );
            }
        }

        application.setStatus(ApplicationStatus.ENTERED);

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS3181(application);
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

        ApplicationS3181 application = record.getApplicationS3181();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3181.class, "null");
        }

        // Add distance trading address
        if (!CollectionUtils.isEmpty(application.getAddresses())) {
            applicant.getAddresses().addAll(application.getAddresses());
        }

        if (!CollectionUtils.isEmpty(application.getApplicationS3181Vehicles())) {
            for (ApplicationS3181Vehicle av : application.getApplicationS3181Vehicles()) {
                Vehicle vehicle = av.getVehicle();
                if (applicant.getContractorVehicles().stream()
                        .noneMatch(cv ->  vehicle.getRegistrationPlate().equals(cv.getVehicle().getRegistrationPlate()))) {
                    vehicle.setEnabled(true);
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, av.getOwnerType());
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
        record.getApplicationS3181().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);
        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        record.getApplicationS3181().setStatus(ApplicationStatus.FOR_CORRECTION);
        mailService.sendApplicationCorrectionNotification(record, message, language);
        return recordRepository.save(record);
    }
}