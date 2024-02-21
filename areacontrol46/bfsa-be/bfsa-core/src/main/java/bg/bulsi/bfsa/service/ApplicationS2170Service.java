package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApplicationS2170DTO;
import bg.bulsi.bfsa.dto.ContractorDTO;
import bg.bulsi.bfsa.dto.eforms.ServiceRequestWrapper;
import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.enums.ApprovalDocumentStatus;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.ApplicationS2170;
import bg.bulsi.bfsa.model.ApplicationS2695;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.ContractorPaperI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.repository.ApplicationS2170Repository;
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
public class ApplicationS2170Service {
    private final ApplicationS2170Repository repository;
    private final ClassifierService classifierService;
    private final BranchService branchService;
    private final BaseApplicationService baseApplicationService;
    private final RecordRepository recordRepository;
    private final NomenclatureService nomenclatureService;
    private final CountryService countryService;
    private final RecordService recordService;
    private final ContractorService contractorService;
    private final SettlementService settlementService;

    private final ServiceType SERVICE_TYPE = ServiceType.S2170;
    public static final String REGISTER_CODE = Constants.CLASSIFIER_REGISTER_0002030_CODE;
    public static final String DIRECTORATE_CODE = Constants.PPP_DIRECTORATE_CODE;

    @Transactional(readOnly = true)
    public ApplicationS2170DTO findByRecordId(final Long recordId, final Language language) {
        return ApplicationS2170DTO.ofRecord(recordService.findById(recordId), language);
    }

    @Transactional(readOnly = true)
    public List<ApplicationS2170DTO> findAllByApplicantIdAndStatus(final Long applicantId, final ApplicationStatus status, final Language language) {
        return ApplicationS2170DTO.of(repository.findAllByRecord_Applicant_IdAndStatus(applicantId, status), language);
    }

    @Transactional
    public Record register(final ApplicationS2170DTO dto, final ServiceRequestWrapper wrapper, final Language language) {
        Branch branch = branchService.findByIdentifier(dto.getBranchIdentifier());
        List<String> errors = new ArrayList<>();
        Classifier register = classifierService.findByCode(REGISTER_CODE);

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

        Contractor supplier = contractorService.findByIdentifierOrNull(dto.getSupplier().getIdentifier());
        if (supplier != null) {
            String errMessagePrefix = "Supplier with identifier: " + supplier.getIdentifier();
            if (!StringUtils.hasText(supplier.getFullName())
                    && StringUtils.hasText(dto.getSupplier().getFullName())) {
                supplier.setFullName(dto.getSupplier().getFullName());
            } else if (StringUtils.hasText(dto.getSupplier().getFullName())
                    && !dto.getSupplier().getFullName().equals(supplier.getFullName())) {
                errors.add(errMessagePrefix + " has name: " + supplier.getFullName() +
                        " which is different than the name (" +
                        dto.getSupplier().getFullName() +
                        ") received from application");
            }
        } else {
            supplier = ContractorDTO.to(dto.getSupplier());
        }

        Address supplierAddress = AddressDTO.to(dto.getSupplier().getAddress());
        supplierAddress.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_SUPPLIER_CODE));
        supplierAddress.setServiceType(SERVICE_TYPE);
        if (StringUtils.hasText(dto.getSupplier().getAddress().getSettlementCode())) {
            supplierAddress.setSettlement(settlementService.findByCode(dto.getSupplier().getAddress().getSettlementCode()));
        }
        supplier.getAddresses().add(supplierAddress);

        Contractor manufacturer = contractorService.findByIdentifierOrNull(dto.getManufacturer().getIdentifier());
        if (manufacturer != null) {
            String errMessagePrefix = "Manufacturer with identifier: " + manufacturer.getIdentifier();
            if (!StringUtils.hasText(manufacturer.getFullName())
                    && StringUtils.hasText(dto.getManufacturer().getFullName())) {
                manufacturer.setFullName(dto.getManufacturer().getFullName());
            } else if (StringUtils.hasText(dto.getManufacturer().getFullName())
                    && !dto.getManufacturer().getFullName().equals(manufacturer.getFullName())) {
                errors.add(errMessagePrefix + " has name: " + manufacturer.getFullName() +
                        " which is different than the name (" +
                        dto.getManufacturer().getFullName() +
                        ") received from application");
            }
        } else {
            manufacturer = ContractorDTO.to(dto.getManufacturer());
        }

        Address manufacturerAddress = AddressDTO.to(dto.getManufacturer().getAddress());
        manufacturerAddress.setAddressType(nomenclatureService.findByCode(Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE));
        manufacturerAddress.setServiceType(SERVICE_TYPE);
        if (StringUtils.hasText(dto.getManufacturer().getAddress().getSettlementCode())) {
            manufacturerAddress.setSettlement(settlementService.findByCode(dto.getManufacturer().getAddress().getSettlementCode()));
        }
        manufacturer.getAddresses().add(manufacturerAddress);

        AddressBO manufactureAddress = dto.getManufactureAddress();

        ApplicationS2170 application = ApplicationS2170.builder()
                .serviceRequestWrapper(wrapper)
                .supplier(supplier)
                .manufacturer(manufacturer)
                .manufactureAddress(manufactureAddress)
                .manufacturePlace(dto.getManufacturePlace())
                .name(dto.getName())
                .nameLat(dto.getNameLat())
                .productCategory(nomenclatureService.findByCode(dto.getProductCategoryCode()))
                .productType(StringUtils.hasText(dto.getProductTypeCode())
                        ? nomenclatureService.findByCode(dto.getProductTypeCode())
                        : null)
                .euMarketPlacementCountry(StringUtils.hasText(dto.getEuMarketPlacementCountryCode())
                        ? countryService.findByCode(dto.getEuMarketPlacementCountryCode())
                        : null)
                .materials(dto.getMaterials())
                .processingDescription(dto.getProcessingDescription())
                .processingDescriptionPatentNumber(dto.getProcessingDescriptionPatentNumber())
                .ingredients(dto.getIngredients())
                .physicalStateCode(nomenclatureService.findByCode(dto.getPhysicalStateCode()))
                .physicalCode(nomenclatureService.findByCode(dto.getPhysicalCode()))
                .drySubstance(dto.getDrySubstance())
                .organicSubstance(dto.getOrganicSubstance())
                .inorganicSubstance(dto.getInorganicSubstance())
                .ph(dto.getPh())
                .arsen(dto.getArsen())
                .nickel(dto.getNickel())
                .cadmium(dto.getCadmium())
                .mercury(dto.getMercury())
                .chrome(dto.getChrome())
                .lead(dto.getLead())
                .livingOrganisms(dto.getLivingOrganisms())
                .enterococci(dto.getEnterococci())
                .enterococciColi(dto.getEnterococciColi())
                .clostridiumPerfringens(dto.getClostridiumPerfringens())
                .salmonella(dto.getSalmonella())
                .staphylococus(dto.getStaphylococus())
                .aspergillus(dto.getAspergillus())
                .nematodes(dto.getNematodes())
                .expectedEffect(dto.getExpectedEffect())
                .crops(dto.getCrops())
                .possibleMixes(dto.getPossibleMixes())
                .notRecommendedMixes(dto.getNotRecommendedMixes())
                .notRecommendedClimaticConditions(dto.getNotRecommendedClimaticConditions())
                .notRecommendedSoilConditions(dto.getNotRecommendedSoilConditions())
                .prohibitedImportCrops(dto.getProhibitedImportCrops())
                .storage(dto.getStorage())
                .transport(dto.getTransport())
                .fire(dto.getFire())
                .humanAccident(dto.getHumanAccident())
                .spilliageAccident(dto.getSpilliageAccident())
                .handlingDeactivationOption(dto.getHandlingDeactivationOption())
                .status(ApplicationStatus.ENTERED)
                .build();

        Record record = baseApplicationService.buildRecord(SERVICE_TYPE, requestor, applicant, branch, dto);
        record.setApplicationS2170(application);
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

        ApplicationS2170 application = record.getApplicationS2170();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2695.class, "null");
        }

        ContractorPaper paper = ContractorPaper.builder()
                .contractor(applicant)
                .serviceType(SERVICE_TYPE)
                .regDate(LocalDate.now())
                .record(record)
                .validUntilDate(null) // TODO: Check where is endDate.
                .status(ApprovalDocumentStatus.ACTIVE)
                .enabled(true)
                .build();

        paper.getI18ns().add(
                new ContractorPaperI18n("ЗАЯВЛЕНИЕ за регистрация на торове, подобрители на почвата, биологично " +
                        "активни вещества и хранителни субстрати и издаване  на удостоверение за пускане на пазара и употреба",
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

        ApplicationS2170 application = record.getApplicationS2170();
        if (application == null) {
            throw new EntityNotFoundException(ApplicationS2170.class, "null");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        record.setStatus(RecordStatus.FINAL_REJECTED);

        return recordRepository.save(record);
    }
}


