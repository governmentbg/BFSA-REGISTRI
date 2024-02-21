package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS2274ActiveSubstanceDTO;
import bg.bulsi.bfsa.dto.ApplicationS2274DTO;
import bg.bulsi.bfsa.dto.ApplicationS2274PackageDTO;
import bg.bulsi.bfsa.dto.ApplicationS2274ReferenceProductDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ApplicationS2274;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
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

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2274Service {
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final CountryService countryService;
    private final NomenclatureService nomenclatureService;
    private final RecordService recordService;
    private final MailService mailService;

    private final ServiceType SERVICE_TYPE = ServiceType.S2274;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002027_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2274DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2274DTO.of(recordService.findById(recordId), language);
    }

    @Transactional
    public Record register(final ApplicationS2274DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        ApplicationS2274 application = ApplicationS2274.builder()
                .serviceRequestWrapper(wrapper)
                .status(ApplicationStatus.ENTERED).build();

        BeanUtils.copyProperties(dto, application);

        if (!CollectionUtils.isEmpty(application.getReferenceProducts())) {
            for (ApplicationS2274ReferenceProductDTO referenceProduct : application.getReferenceProducts()) {
                if (StringUtils.hasText(referenceProduct.getCountryCode())) {
                    referenceProduct.setCountryName(countryService.findByCode(referenceProduct.getCountryCode()).getI18n(language).getName());
                }
            }
        }
        if (!CollectionUtils.isEmpty(application.getPackages())) {
            for (ApplicationS2274PackageDTO pack : application.getPackages()) {
                if (!CollectionUtils.isEmpty(pack.getQuantities())) {
                    for (KeyValueDTO quantity : pack.getQuantities()) {
                        quantity.setName(nomenclatureService.findByCode(quantity.getCode()).getI18n(language).getName());
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(application.getActiveSubstances())) {
            for (ApplicationS2274ActiveSubstanceDTO substance : application.getActiveSubstances()) {
                substance.setManufacturerCountryName(countryService
                        .findByCode(substance.getManufacturerCountryCode()).getI18n(language).getName());
                if (StringUtils.hasText(substance.getSubstanceQuantityUnitCode())) {
                    substance.setSubstanceQuantityUnitName(
                            nomenclatureService.findByCode(substance.getSubstanceQuantityUnitCode())
                                    .getI18n(language).getName());
                }
                if (!CollectionUtils.isEmpty(dto.getPppActiveSubstanceTypes())) {
                    for (KeyValueDTO type : dto.getPppActiveSubstanceTypes()) {
                        substance.getPppActiveSubstanceTypes().add(
                                KeyValueDTO.builder()
                                        .code(type.getCode())
                                        .name(nomenclatureService.findByCode(type.getCode()).getI18n(language).getName())
                                        .build());
                    }
                }
            }
        }
        if (StringUtils.hasText(dto.getPppCategoryCode())) {
            application.setPlantProtectionProductCategory(nomenclatureService.findByCode(dto.getPppCategoryCode()));
        }
        if (StringUtils.hasText(dto.getPppFormulationTypeCode())) {
            application.setFormulationType(nomenclatureService.findByCode(dto.getPppFormulationTypeCode()));
        }
        if (!CollectionUtils.isEmpty(dto.getPppFunctions())) {
            application.getPlantProtectionProductFunctions().clear();
            for (KeyValueDTO function : dto.getPppFunctions()) {
                application.getPlantProtectionProductFunctions()
                        .add(nomenclatureService.findByCode(function.getCode()));
            }
        }
        if (!CollectionUtils.isEmpty(dto.getPppActions())) {
            application.getPlantProtectionProductActions().clear();
            for (KeyValueDTO action : dto.getPppActions()) {
                application.getPlantProtectionProductActions()
                        .add(nomenclatureService.findByCode(action.getCode()));
            }
        }

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2274(application);
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
        if (record == null) {
            throw new EntityNotFoundException(Record.class, "null");
        }
        Contractor applicant = record.getApplicant();
        if (applicant == null) {
            throw new EntityNotFoundException(Contractor.class, "null");
        }
        ApplicationS2274 application = record.getApplicationS2274();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2274.class, "null");
        }

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .record(record)
                .regDate(LocalDate.now())
                // TODO Is the validity 10 years?
//                .validUntilDate(LocalDate.now().plusYears(10L))
                .build();
        paper.getI18ns().add(
                new ContractorPaperI18n(SERVICE_TYPE.name(),
                        "ЗАЯВЛЕНИЕ зза разрешаване за пускане на пазара и употреба на продукт " +
                                "за растителна защита за паралелна търговия по чл. 61 ал. 1 от " +
                                "ЗЗР (чл. 52 от Регламент (ЕО) No 1107/2009)",
                        null, paper, language)
        );

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());
        applicant.getContractorPapers().add(paper);
        record.setContractorPaper(paper);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);
        return recordService.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);
        record.getApplicationS2274().setStatus(ApplicationStatus.REJECTED);
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
