package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS2869DTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS2869;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2869Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final NomenclatureService nomenclatureService;
    private final FacilityService facilityService;
    private final VehicleService vehicleService;
    private final RecordService recordService;
    private final MailService mailService;

    private final ServiceType SERVICE_TYPE = ServiceType.S2869;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002023_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2869DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2869DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS2869DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
//        TODO: особено за тази услуга е, че се предоставя от ЦУ
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

        ApplicationS2869 application = ApplicationS2869.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED)
                .commencementActivityDate(dto.getCommencementActivityDate()).build();

        if (!CollectionUtils.isEmpty(dto.getFacilities())) {
            for (FacilityDTO f : dto.getFacilities()) {
                Facility entity = facilityService.findByRegNumberOrNull(f.getRegNumber());
                if (entity == null) {
                    errors.add("Facility with reg number: " + f.getRegNumber() + " doesn't exist.");
                } else if (CollectionUtils.isEmpty(f.getActivityTypes())) {
                    errors.add("Facility with reg number: " + f.getRegNumber() + " has no activities");
                } else {
                    FacilityDTO facility = FacilityDTO.baseOf(entity, language);
                    for (KeyValueDTO activity : f.getActivityTypes()) {
                        Nomenclature a = nomenclatureService.findByCode(activity.getCode());
                        facility.getActivityTypes().add(KeyValueDTO.builder().code(a.getCode()).name(a.getI18n(language).getName()).build());
                    }
                    application.getFacilities().add(facility);
                }
            }
        }

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

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2869(application);
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

        ApplicationS2869 application = record.getApplicationS2869();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2869.class, "null");
        }

        List<FacilityDTO> facilities = application.getFacilities();
        if (CollectionUtils.isEmpty(facilities)) {
            throw new EntityNotFoundException(Facility.class, "null");
        }

        Classifier register = classifierService.findByCode(Constants.CLASSIFIER_REGISTER_0002023_CODE);
        facilities.forEach(f -> {
            Facility facility = facilityService.findByRegNumber(f.getRegNumber());

            // TODO Add all activities to the facility if not contains (it is not clear by BFSA)

            if (facility.getRegisters().stream().noneMatch(r -> r.getCode().equals(register.getCode()))) {
                facility.getRegisters().add(register);
            }

            if (applicant.getContractorFacilities().stream().noneMatch(cf -> facility.getId().equals(cf.getFacility().getId())
                    && SERVICE_TYPE.equals(cf.getServiceType()))) {
                f.setEnabled(true);
                applicant.addContractorFacility(facility, SERVICE_TYPE);
            }
        });

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

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .validUntilDate(LocalDate.now().plusYears(5L))
                .status(ApprovalDocumentStatus.ACTIVE)
                .record(record)
                .build();
        paper.getI18ns().add(
                new ContractorPaperI18n(SERVICE_TYPE.name(),
                        "ЗАЯВЛЕНИЕ за издаване на разрешение за оператор на хранителна банка",
                        null, paper, language)
        );

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());

        applicant.getContractorPapers().add(paper);
        record.setContractorPaper(paper);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        ApplicationS2869 application = record.getApplicationS2869();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2869.class, "null");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }

        ApplicationS2869 application = record.getApplicationS2869();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2869.class, "null");
        }

        application.setStatus(ApplicationStatus.FOR_CORRECTION);
        record.setStatus(RecordStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }
}


