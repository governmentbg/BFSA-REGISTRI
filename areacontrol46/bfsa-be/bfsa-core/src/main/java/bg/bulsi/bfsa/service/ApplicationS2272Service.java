package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS2272DTO;
import bg.bulsi.bfsa.dto.ApproveFoodTypesDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.enums.VehicleStatus;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS2272;
import bg.bulsi.bfsa.model.ApplicationS2272Vehicle;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2272Service {
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final VehicleService vehicleService;
    private final NomenclatureService nomenclatureService;
    private final BranchService branchService;
    private final MailService mailService;
    private final BaseApplicationService baseApplicationService;
    private final ClassifierService classifierService;
    private final ServiceType SERVICE_TYPE = ServiceType.S2272;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002018_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2272DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2272DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS2272DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        ApplicationS2272 application = ApplicationS2272.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();

        //--- Използвам МПС за транспортиране на храни по чл. 55 ---
        if (!CollectionUtils.isEmpty(dto.getVehicles())) {
            for (VehicleDTO v : dto.getVehicles()) {
                if (!StringUtils.hasText(v.getRegistrationPlate())) {
                    errors.add("Can't register vehicle without registration plate");
                } else if (vehicleService.existsByRegistrationPlateAndCertificateNumberIsNotNull(v.getRegistrationPlate())) {
                    errors.add("The vehicle with registration plate " + v.getRegistrationPlate() + " already exists");
                }
                if (!CollectionUtils.isEmpty(v.getFoodTypes())) {
                    Set<Classifier> foodTypesWithParent = new HashSet<>();
                    for (KeyValueDTO keyValue : v.getFoodTypes()) {
                        Classifier foodType = classifierService.findByCode(keyValue.getCode());
                        foodTypesWithParent.add(foodType);
                    }
                    v.setFoodTypes(KeyValueDTO.ofClassifiers(foodTypesWithParent, language));
                }
            }
            application.setCh55vehicles(dto.getVehicles());
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2272(application);
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

        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS2272 application = record.getApplicationS2272();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2272.class, "null");
        }

        final List<String> errors = new ArrayList<>();
        if (!CollectionUtils.isEmpty(application.getCh55vehicles())) {
            for (VehicleDTO dto : application.getCh55vehicles()) {
                if (!CollectionUtils.isEmpty(approvedFoodTypes)) {
                    approvedFoodTypes.stream()
                            .filter(ft -> ft.getIdentifier().equals(dto.getRegistrationPlate())).findAny().ifPresent(
                                    approvedFoodType -> dto.setFoodTypes(approvedFoodType.getFoodTypes()));
                }

                Vehicle vehicle = baseApplicationService.buildVehicle(dto, classifierService.findByCode(REGISTER_CODE), record.getBranch(), errors, language);

                vehicle.setEnabled(true);
                vehicle.setStatus(VehicleStatus.ACTIVE);
                vehicle.setCertificateDate(LocalDate.now());
                // TODO: Set correct logic for certificate number
                vehicle.setCertificateNumber(UUID.randomUUID().toString().substring(0, 6));

                Nomenclature ownershipType = nomenclatureService.findByCode(dto.getVehicleOwnershipTypeCode());

                application.getApplicationS2272Vehicles().add(
                        ApplicationS2272Vehicle.builder()
                                .ownerType(ownershipType)
                                .applicationS2272(application)
                                .vehicle(vehicle).build()
                );

                if (CollectionUtils.isEmpty(applicant.getContractorVehicles()) ||
                        applicant.getContractorVehicles().stream().noneMatch(cf ->
                                vehicle.getRegistrationPlate().equals(cf.getVehicle().getRegistrationPlate()))) {
                    applicant.addContractorVehicle(vehicle, SERVICE_TYPE, ownershipType);
                }
            }
        }

        record.getApplicationS2272().setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS2272().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        // TODO Да се откоментира в случай, че имаме вариант в който
        //  отказваме услугата след като е вече била одобрена.
//        if (!record.getApplicationS2272().getVehicles().isEmpty()) {
//            record.getApplicationS2272().getVehicles().forEach(v -> {
//                v.setCertificateDate(null);
//                v.setCertificateNumber(null);
//            });
//            record.getApplicant().getVehicles().removeAll(record.getApplicationS2272().getVehicles());
//        }

        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        record.getApplicationS2272().setStatus(ApplicationStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }
}