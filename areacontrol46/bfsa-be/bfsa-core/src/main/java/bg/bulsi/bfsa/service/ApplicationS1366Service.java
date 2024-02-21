package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS1366DTO;
import bg.bulsi.bfsa.dto.ApplicationS1366ProductBatchDTO;
import bg.bulsi.bfsa.dto.ApplicationS1366ProductDTO;
import bg.bulsi.bfsa.dto.ApproveFoodTypesDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS1366;
import bg.bulsi.bfsa.model.ApplicationS1366Product;
import bg.bulsi.bfsa.model.ApplicationS1366ProductBatch;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
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
public class ApplicationS1366Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final NomenclatureService nomenclatureService;
    private final RecordService recordService;
    private final CountryService countryService;
    private final FacilityService facilityService;
    private final MailService mailService;

    private final ServiceType SERVICE_TYPE = ServiceType.S1366;
    public static final String DIRECTORATE_CODE = Constants.FOOD_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS1366DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS1366DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS1366DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        Classifier register = classifierService.findByCode(StringUtils.hasText(dto.getFacilityRegNumber())
                ? Constants.CLASSIFIER_REGISTER_0002021_CODE
                : Constants.CLASSIFIER_REGISTER_0002020_CODE);

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

        ApplicationS1366 application = ApplicationS1366.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED)
                .recipientName(dto.getRecipientName())
                .recipientCountry(StringUtils.hasText(dto.getRecipientCountryCode())
                        ? countryService.findByCode(dto.getRecipientCountryCode())
                        : null)
                .recipientAddress(dto.getRecipientAddress())
                .borderCrossing(StringUtils.hasText(dto.getBorderCrossingCode())
                        ? nomenclatureService.findByCode(dto.getBorderCrossingCode())
                        : null)
                .applicantType(StringUtils.hasText(dto.getApplicantTypeCode())
                        ? nomenclatureService.findByCode(dto.getApplicantTypeCode())
                        : null)
                .build();

        Facility facility = facilityService.findByRegNumberOrNull(dto.getFacilityRegNumber());
        if (facility != null) {
            application.setFacility(facility);
        } else {
            errors.add("Facility with registration number " + dto.getFacilityRegNumber() + " does not exist.");
        }

        if (!CollectionUtils.isEmpty(dto.getProducts())) {
            for (ApplicationS1366ProductDTO p : dto.getProducts()) {
                ApplicationS1366Product product = ApplicationS1366ProductDTO.to(p);
                product.setApplicationS1366(application);

                // Check if the foodType classifier exists and add its parents
                if (!CollectionUtils.isEmpty(p.getFoodTypes())) {
                    Set<Classifier> foodTypesWithParent = new HashSet<>();
                    for (KeyValueDTO keyValue : p.getFoodTypes()) {
                        Classifier foodType = classifierService.findByCode(keyValue.getCode());
                        foodTypesWithParent.add(foodType);
                    }
                    product.setApplicationS1366ProductFoodTypes(KeyValueDTO.ofClassifiers(foodTypesWithParent, language));
                }

                if (StringUtils.hasText(p.getProductCountryCode())) {
                    product.setProductCountry(countryService.findByCode(p.getProductCountryCode()));
                }
                if (StringUtils.hasText(p.getProductTotalNetWeightUnitCode())) {
                    product.setProductTotalNetWeightUnit(nomenclatureService.findByCode(p.getProductTotalNetWeightUnitCode()));
                }
                if (!CollectionUtils.isEmpty(p.getBatches())) {
                    for (ApplicationS1366ProductBatchDTO b : p.getBatches()) {
                        ApplicationS1366ProductBatch batch = ApplicationS1366ProductBatchDTO.to(b);
                        batch.setApplicationS1366Product(product);
                        if (StringUtils.hasText(b.getPerUnitNetWeightUnitCode())) {
                            batch.setPerUnitNetWeightUnit(nomenclatureService.findByCode(b.getPerUnitNetWeightUnitCode()));
                        }
                        if (StringUtils.hasText(b.getBatchNetWeightUnitCode())) {
                            batch.setBatchNetWeightUnit(nomenclatureService.findByCode(b.getBatchNetWeightUnitCode()));
                        }
                        product.getApplicationS1366ProductBatches().add(batch);
                    }
                }
                application.getApplicationS1366Products().add(product);
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS1366(application);
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

        ApplicationS1366 application = record.getApplicationS1366();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS1366.class, "null");
        }

        Facility facility = application.getFacility();
        if (facility == null) {
            throw new EntityNotFoundException(Facility.class, "null");
        }

        if (!CollectionUtils.isEmpty(application.getApplicationS1366Products())) {
            for (ApplicationS1366Product product : application.getApplicationS1366Products()) {
                if (!CollectionUtils.isEmpty(approvedFoodTypes)) {
                    approvedFoodTypes.stream()
                            .filter(aft -> aft.getIdentifier().equals(product.getId().toString()))
                            .findAny().ifPresent(aft -> aft.getFoodTypes().forEach(ft -> product.getFoodTypes().add(classifierService.findByCode(ft.getCode()))));
                }
            }
        }

//        TODO: Check with Galia. To be disscust activityDescrioption
        applicant.addContractorFacility(facility, SERVICE_TYPE);

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .regDate(LocalDate.now())
                .regNumber(UUID.randomUUID().toString())
                .validUntilDate(LocalDate.now().plusMonths(1L)) // TODO: May be will be changed
                .record(record)
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .build();
        paper.getI18ns().add(
                new ContractorPaperI18n("ЗДРАВЕН СЕРТИФИКАТ / HEALTH CERTIFICATE / САНИТАРНЫЙ СЕРТИФИКАТ" +
                        "за износ на храни и/или материали и предмети в контакт с храни " +
                        "/ for export of foodstuffs and/or materials and articles intended to come into contact with foodstuffs " +
                        "/ для вывоза пищи и/или материалов и предметов, которые вступают в контакт с пищи",
                        null,
                        paper,
                        language)
        );

        applicant.getContractorPapers().add(paper);
        record.setContractorPaper(paper);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);
        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS1366().setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);
        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        record.getApplicationS1366().setStatus(ApplicationStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }

}
