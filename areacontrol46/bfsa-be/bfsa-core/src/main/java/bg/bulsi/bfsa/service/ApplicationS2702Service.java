package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ApplicationS2702DTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS2702;
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
public class ApplicationS2702Service {

    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final NomenclatureService nomenclatureService;
    private final SettlementService settlementService;
    private final CountryService countryService;
    private final ClassifierService classifierService;
    private final RecordRepository recordRepository;
    private final RecordService recordService;
    private final MailService mailService;
    private final DocumentService documentService;

    private final ServiceType SERVICE_TYPE = ServiceType.S2702;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002043_CODE;
    public static final String DIRECTORATE_CODE = Constants.PHYTO_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2702DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2702DTO.ofRecord(recordService.findById(recordId), language);
    }

//    @Transactional(readOnly = true)
//    public List<ApplicationS2702DTO> findAllByApplicantIdAndStatus(final Long applicantId, final ApplicationStatus status, final Language language) {
//        return ApplicationS2702DTO.of(repository.findAllByApplicant_IdAndStatus(applicantId, status), language);
//    }

    @Transactional
    public Record register(final ApplicationS2702DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
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

        applicant.setEntityType(EntityType.LEGAL);

        ApplicationS2702 application = ApplicationS2702.builder()
                .serviceRequestWrapper(wrapper)
                .materialName(dto.getMaterialName())
                .materialType(dto.getMaterialType())
                .materialTotalAmount(dto.getMaterialTotalAmount())
                .materialPackagingCondition(dto.getMaterialPackagingCondition())
                .materialMeasuringUnitCode(nomenclatureService.findByCode(dto.getMaterialMeasuringUnitCode()))
                .materialOriginCountry(countryService.findByCode(dto.getMaterialOriginCountryCode()))
                .materialExportCountry(
                        StringUtils.hasText(dto.getMaterialExportCountryCode())
                                ? countryService.findByCode(dto.getMaterialExportCountryCode())
                                : countryService.findByCode(dto.getMaterialOriginCountryCode()))
                .status(ApplicationStatus.ENTERED).build();

        application.getMaterialMovements().addAll(dto.getMaterialMovements());

//        --- Set Activity responsible persons ---
        if (!CollectionUtils.isEmpty(dto.getActivityResponsiblePersons())) {
            dto.getActivityResponsiblePersons().forEach(p ->
                    application.getActivityResponsiblePersons().add(baseApplicationService.buildContractor(p, errors)));
        }

//        --- Set supplier data ---
        Contractor supplier = baseApplicationService.buildContractor(dto.getSupplier(), errors);
        supplier.setContractorActivityType(nomenclatureService
                .findByCode(dto.getSupplier().getContractorActivityTypeCode()));

        Address supplierAddress = Address.builder()
                .addressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_SUPPLIER_CODE))
                .phone(dto.getSupplierAddress().getPhone())
                .enabled(true)
                .address(dto.getSupplierAddress().getAddress())
                .fullAddress(dto.getSupplierAddress().getFullAddress())
                .settlementName(!StringUtils.hasText(dto.getSupplierAddress().getSettlementCode())
                        ? dto.getSupplierAddress().getSettlementName()
                        : null)
                .build();

        if (StringUtils.hasText(dto.getSupplierAddress().getCountryCode())) {
            supplierAddress.setCountry(countryService.findByCode(dto.getSupplierAddress().getCountryCode()));
        }
        if (StringUtils.hasText(dto.getSupplierAddress().getSettlementCode())) {
            supplierAddress.setSettlement(settlementService.findByCode(dto.getSupplierAddress().getSettlementCode()));
        }

        application.setSupplier(supplier);
        application.setSupplierAddress(supplierAddress);

//        --- Set Quarantine Station ---
        application.setQuarantineStationName(dto.getQuarantineStationName());
        application.setQuarantineStationDescription(dto.getQuarantineStationDescription());
        application.setQuarantineStationAddress(dto.getQuarantineStationAddress().getFullAddress());
        application.setQuarantineStationMaterialStorageMeasure(dto.getQuarantineStationMaterialStorageMeasure());
//        --- Set Quarantine Station Person ---
        application.setQuarantineStationPerson(baseApplicationService.buildContractor(dto.getQuarantineStationPerson(), errors));
//        --- Set Activity Summary ---
        application.setRequestedActivitySummary(dto.getRequestedActivitySummary());
        application.setFirstEntryDate(dto.getFirstEntryDate());
        application.setExpectedCompletionDate(dto.getExpectedCompletionDate());
        application.setMaterialEndUse(nomenclatureService.findByCode(dto.getMaterialEndUseCode()));
        application.setMaterialDestructionMethod(dto.getMaterialDestructionMethod());
        application.setMaterialSafeMeasure(dto.getMaterialSafeMeasure());
        application.setDescription(dto.getDescription());

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2702(application);
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

        ApplicationS2702 application = record.getApplicationS2702();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2702.class, "null");
        }

        // TODO Check me
        applicant.getAddresses().add(application.getSupplierAddress());

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .regDate(LocalDate.now())
                .record(record)
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .build();

        paper.getI18ns().add(
                new ContractorPaperI18n("ЗАЯВЛЕНИЕ за временно разрешаване на въвеждане, движение, съхранение и" +
                        " размножаване на карантинни вредители на територията на Европейския Съюз, както и за" +
                        " въвеждане и движение на територията на Съюза на растения, растителни продукти и други" +
                        " обекти, за официални изпитвания, научноизследователски или образователни цели, опити, " +
                        "сортов подбор или селекция",
                        null,
                        paper,
                        language)
        );

        baseApplicationService.generateRegNumber(paper, record.getBranch().getSequenceNumber());

        applicant.getContractorPapers().add(paper);
        record.setContractorPaper(paper);

        documentService.exportPaper(record.getId(), language);

        application.setStatus(ApplicationStatus.APPROVED);
        record.setStatus(RecordStatus.FINAL_APPROVED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record refuse(final Long recordId) {
        Record record = recordService.findById(recordId);

        ApplicationS2702 application = record.getApplicationS2702();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2702.class, "null");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordRepository.save(record);
    }

    @Transactional
    public Record forCorrection(final Long recordId, String message, final Language language) {
        Record record = recordService.findById(recordId);
        record.setStatus(RecordStatus.FOR_CORRECTION);
        record.getApplicationS2702().setStatus(ApplicationStatus.FOR_CORRECTION);

        mailService.sendApplicationCorrectionNotification(record, message, language);

        return recordRepository.save(record);
    }
}
