package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS7691DTO;
import bg.bulsi.bfsa.dto.ApplicationS769DTO;
import bg.bulsi.bfsa.dto.ApproveFoodTypesDTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.FishingVesselDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS7691;
import bg.bulsi.bfsa.model.ApplicationS7691FishingVessel;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FishingVessel;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS7691Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordService recordService;
    private final FishingVesselService fishingVesselService;
    private final NomenclatureService nomenclatureService;
    private final RecordRepository recordRepository;
    private final MailService mailService;
    private final CountryService countryService;
    private final SettlementService settlementService;

    private final ServiceType SERVICE_TYPE = ServiceType.S7691;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;
    public static String REGISTER_CODE;

    @Transactional(readOnly = true)
    public ApplicationS769DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS7691DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS7691DTO dto, final ServiceRequestWrapper wrapper, final Language language) {

        String activityTypeCode = dto.getFacility().getActivityTypeCode();
        switch (activityTypeCode) {
            case Constants.ACTIVITY_TYPE_PRIMARY_PRODUCT_NON_ANIMAL_AGRICULTURAL_CODE,
                    Constants.ACTIVITY_TYPE_PRIMARY_PRODUCT_NON_ANIMAL_WILD_CODE:
                REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002013_CODE;
                break;
            case Constants.ACTIVITY_TYPE_PRIMARY_PRODUCT_ANIMAL_FARM_CODE,
                    Constants.ACTIVITY_TYPE_PRIMARY_PRODUCT_ANIMAL_HUNTING_CODE,
                    Constants.ACTIVITY_TYPE_PRIMARY_PRODUCT_ANIMAL_CATCH_CODE:
                REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002014_CODE;
                break;
            case Constants.ACTIVITY_TYPE_FOOD_ANIMAL_CODE,
                    Constants.ACTIVITY_TYPE_FOOD_NON_ANIMAL_CODE:
                REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002015_CODE;
                break;
            default:
                throw new RuntimeException("Unknown activity type " + activityTypeCode);
        }

        Branch branch = null;
        if (StringUtils.hasText(dto.getBranchIdentifier())) {
            branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        }

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

        ApplicationS7691 application = ApplicationS7691.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED)
                .commencementActivityDate(dto.getCommencementActivityDate())
                .build();

        BeanUtils.copyProperties(dto, application);

        if (!CollectionUtils.isEmpty(dto.getFishingVessels())) {
            for (FishingVesselDTO fishingVessel : dto.getFishingVessels()) {
                if (application.getApplicationS7691FishingVessels().stream().noneMatch(fv -> fishingVessel.getRegNumber().equals(fv.getFishingVessel().getRegNumber()))) {
                    FishingVessel vessel = fishingVesselService.findByRegNumber(fishingVessel.getRegNumber());
                    if (vessel != null) {
                        application.addApplicationS7691FishingVessel(vessel, null);
                        errors.add("Fishing vessel with registration number: " + fishingVessel.getRegNumber() + " exists.");
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(dto.getFishingVessels())) {
            for (FishingVesselDTO fishingVessel : dto.getFishingVessels()) {
                if (!StringUtils.hasText(fishingVessel.getRegNumber())) {
                    throw new RuntimeException("Fishing vessel registration number is required");
                }
                FishingVessel vessel = baseApplicationService
                        .buildFishingVessel(fishingVessel, branch, errors, language);

                application.getApplicationS7691FishingVessels().add(
                        ApplicationS7691FishingVessel.builder()
                                .ownershipType(null) // TODO: Да се добави вид собственост, когато се уточни дали ще има такава
                                .applicationS7691(application)
                                .fishingVessel(vessel)
                                .build()
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
            facility.setMeasuringUnit(StringUtils.hasText(dto.getFacility().getMeasuringUnitCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getMeasuringUnitCode())
                    : null);
            facility.setPeriod(StringUtils.hasText(dto.getFacility().getPeriodCode())
                    ? nomenclatureService.findByCode(dto.getFacility().getPeriodCode())
                    : null);
            facility.getI18n(language).setDisposalWasteWater(StringUtils.hasText(dto.getFacility().getDisposalWasteWater())
                    ? dto.getFacility().getDisposalWasteWater()
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
            application.setAddress(
                    Address.builder()
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

        // Check if the foodType classifier exists and add its parents
        if (!CollectionUtils.isEmpty(dto.getPrimaryProductFoodTypes())) {
            Set<Classifier> foodTypesWithParent = new HashSet<>();
            for (KeyValueDTO keyValue : dto.getPrimaryProductFoodTypes()) {
                Classifier foodType = classifierService.findByCode(keyValue.getCode());
                foodTypesWithParent.add(foodType);
            }
            application.setApplicationS7691PrimaryProductFoodTypes(KeyValueDTO.ofClassifiers(foodTypesWithParent, language));
        }

        // Check if the foodType classifier exists and add its parents
        if (!CollectionUtils.isEmpty(dto.getProducedFoodTypes())) {
            Set<Classifier> foodTypesWithParent = new HashSet<>();
            for (KeyValueDTO keyValue : dto.getProducedFoodTypes()) {
                Classifier foodType = classifierService.findByCode(keyValue.getCode());
                foodTypesWithParent.add(foodType);
            }
            application.setApplicationS7691ProducedFoodTypes(KeyValueDTO.ofClassifiers(foodTypesWithParent, language));
        }

        if (StringUtils.hasText(dto.getGameProcessingPeriodCode())) {
            application.setGameProcessingPeriod(nomenclatureService.findByCode(dto.getGameProcessingPeriodCode()));
        }
        if (StringUtils.hasText(dto.getGameStationCapacityUnitCode())) {
            application.setGameStationCapacityUnit(nomenclatureService.findByCode(dto.getGameStationCapacityUnitCode()));
        }

        if (StringUtils.hasText(dto.getDeliveryPeriodCode())) {
            application.setDeliveryPeriod(nomenclatureService.findByCode(dto.getDeliveryPeriodCode()));
        }
        if (StringUtils.hasText(dto.getDeliveryFacilityCapacityUnitCode())) {
            application.setDeliveryFacilityCapacityUnit(nomenclatureService.findByCode(dto.getDeliveryFacilityCapacityUnitCode()));
        }

        if (StringUtils.hasText(dto.getFoodAnnualCapacityUnitCode())) {
            application.setFoodAnnualCapacityUnit(nomenclatureService.findByCode(dto.getFoodAnnualCapacityUnitCode()));
        }
        if (StringUtils.hasText(dto.getFoodPeriodCode())) {
            application.setFoodPeriod(nomenclatureService.findByCode(dto.getFoodPeriodCode()));
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS7691(application);
        record.setDirectorate(classifierService.findByCode(DIRECTORATE_CODE));

        application.setRecord(record);

        if (!CollectionUtils.isEmpty(errors)) {
            record.getErrors().addAll(errors);
        }

        return recordService.save(record);
    }

    @Transactional
    public Record approve(final Long recordId, final List<ApproveFoodTypesDTO> approvedFoodTypes) {
        Record record = recordService.findById(recordId);
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }
        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }

        ApplicationS7691 application = record.getApplicationS7691();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS7691.class, "null");
        }

        if (!CollectionUtils.isEmpty(approvedFoodTypes)) {
            approvedFoodTypes.stream()
                    .filter(ft -> "productFoodTypes".equals(ft.getIdentifier())).findAny()
                    .ifPresent(a -> a.getFoodTypes().forEach(
                            ft -> application.getPrimaryProductFoodTypes().add(classifierService.findByCode(ft.getCode()))
                    ));

            approvedFoodTypes.stream()
                    .filter(ft -> "producedFoodTypes".equals(ft.getIdentifier())).findAny()
                    .ifPresent(a -> a.getFoodTypes().forEach(
                            ft -> application.getProducedFoodTypes().add(classifierService.findByCode(ft.getCode()))
                    ));
        }

        if (!CollectionUtils.isEmpty(application.getApplicationS7691FishingVessels())) {
            for (ApplicationS7691FishingVessel fva : application.getApplicationS7691FishingVessels()) {
                FishingVessel fishingVessel = fva.getFishingVessel();
                if (applicant.getContractorFishingVessels().stream()
                        .noneMatch(fv -> fishingVessel.getRegNumber().equals(fv.getFishingVessel().getRegNumber()))) {
                    fishingVessel.setEnabled(true);
                    applicant.addContractorFishingVessel(fishingVessel, SERVICE_TYPE, fva.getOwnershipType());
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
        record.getApplicationS7691().setStatus(ApplicationStatus.REJECTED);
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
