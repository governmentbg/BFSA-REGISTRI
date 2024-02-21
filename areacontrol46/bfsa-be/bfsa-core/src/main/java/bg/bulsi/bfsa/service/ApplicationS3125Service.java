package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS3125DTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.FoodSupplementStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS3125;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.FoodSupplement;
import bg.bulsi.bfsa.model.FoodSupplementI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.RecordRepository;
import bg.bulsi.bfsa.util.Constants;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS3125Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final CountryService countryService;
    private final NomenclatureService nomenclatureService;
    private final SettlementService settlementService;
    private final MailService mailService;

    private final ServiceType SERVICE_TYPE = ServiceType.S3125;
    public static final String CLASSIFIER_REGISTER_0002017_CODE = Constants.CLASSIFIER_REGISTER_0002017_CODE;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS3125DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS3125DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS3125DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(CLASSIFIER_REGISTER_0002017_CODE);
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

        ApplicationS3125 application = ApplicationS3125.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED)
                .distanceTradingAddress(dto.getDistanceTradingAddress())
                .commencementActivityDate(dto.getCommencementActivityDate())
                .applicantType(StringUtils.hasText(dto.getApplicantTypeCode())
                        ? nomenclatureService.findByCode(dto.getApplicantTypeCode())
                        : null)
                .build();

        if (!CollectionUtils.isEmpty(dto.getFoodSupplements())) {
            dto.getFoodSupplements().forEach(foodSupplementDTO -> {
                FoodSupplement foodSupplement = new FoodSupplement();
                BeanUtils.copyProperties(foodSupplementDTO, foodSupplement);
                foodSupplement.setApplicant(applicant);
                foodSupplement.setFoodSupplementType(StringUtils.hasText(foodSupplementDTO.getFoodSupplementTypeCode())
                        ? nomenclatureService.findByCode(foodSupplementDTO.getFoodSupplementTypeCode())
                        : null);
                foodSupplement.setMeasuringUnit(StringUtils.hasText(foodSupplementDTO.getMeasuringUnitCode())
                        ? nomenclatureService.findByCode(foodSupplementDTO.getMeasuringUnitCode())
                        : null);
                foodSupplement.setFacilityType(StringUtils.hasText(foodSupplementDTO.getFacilityTypeCode())
                        ? nomenclatureService.findByCode(foodSupplementDTO.getFacilityTypeCode())
                        : null);
                if (!CollectionUtils.isEmpty(foodSupplementDTO.getCountries())) {
                    foodSupplement.getCountries().clear();
                    foodSupplementDTO.getCountries()
                            .forEach(c -> foodSupplement.getCountries().add(countryService.findByCode(c.getCode())));
                }
                if (StringUtils.hasText(foodSupplementDTO.getName())) {
                    foodSupplement.getI18ns().add(
                            new FoodSupplementI18n(
                                    foodSupplementDTO.getName(),
                                    foodSupplementDTO.getPurpose(),
                                    foodSupplementDTO.getIngredients(),
                                    foodSupplementDTO.getDescription(),
                                    foodSupplementDTO.getManufactureCompanyName(),
                                    foodSupplement, language)
                    );
                }
                foodSupplement.setApplicationS3125(application);
                application.getFoodSupplements().add(foodSupplement);
            });

        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS3125(application);
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
        ApplicationS3125 application = record.getApplicationS3125();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3125.class, "null");
        }
        List<FoodSupplement> foodSupplements = application.getFoodSupplements();
        if (!CollectionUtils.isEmpty(foodSupplements)) {
            foodSupplements.forEach(fs -> {
                // TODO:  Implement correct logic for serial number
                fs.setRegNumber(UUID.randomUUID().toString().substring(0, 6));
                fs.setRegDate(LocalDate.now());
                fs.setEnabled(true);
                fs.setStatus(FoodSupplementStatus.ACTIVE);
                fs.setApplicant(applicant);
            });
        }

        // Add distance trading address
        if (application.getDistanceTradingAddress() != null) {
            Address address = AddressDTO.to(application.getDistanceTradingAddress());
            address.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_DISTANCE_TRADING_COMMUNICATION_CODE));
            address.setSettlement(settlementService.findByCode(application.getDistanceTradingAddress().getSettlementCode()));
            applicant.getAddresses().add(address);
        }

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
        ApplicationS3125 application = record.getApplicationS3125();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3125.class, "null");
        }
        if (!CollectionUtils.isEmpty(application.getFoodSupplements())) {
            application.getFoodSupplements().forEach(fs -> fs.setEnabled(false));
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

        ApplicationS3125 application = record.getApplicationS3125();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS3125.class, "null");
        }

        application.setStatus(ApplicationStatus.FOR_CORRECTION);
        record.setStatus(RecordStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }
}
