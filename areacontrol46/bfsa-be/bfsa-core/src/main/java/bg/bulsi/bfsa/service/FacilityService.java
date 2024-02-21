package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.InspectionStatus;
import bg.bulsi.bfsa.enums.InspectionType;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorFacility;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityCapacity;
import bg.bulsi.bfsa.model.FacilityI18n;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.repository.ContractorFacilityRepository;
import bg.bulsi.bfsa.repository.FacilityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FacilityService {

    private final FacilityRepository repository;
    private final ContractorFacilityRepository contractorFacilityRepository;
    private final AddressService addressService;
    private final ContractorService contractorService;
    private final ClassifierService classifierService;
    private final UserService userService;
    private final NomenclatureService nomenclatureService;
    private final RecordService recordService;
    private final InspectionService inspectionService;

    public Facility findById(final Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Facility.class, id));
    }

    @Transactional(readOnly = true)
    public FacilityDTO getById(final Long id, final Language language) {
        return FacilityDTO.of(repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Facility.class, id)), language);
    }

    @Transactional(readOnly = true)
    public Facility findByIdOrNull(final Long id) {
        return repository.findById(id).orElse(null);
    }

    public Facility findByRegNumberOrNull(final String regNumber) {
        return StringUtils.hasText(regNumber)
                ? repository.findByRegNumber(regNumber).orElse(null)
                : null;
    }

    @Transactional(readOnly = true)
    public List<Facility> findAll() {
        return repository.findAll();
    }

//    @Transactional
//    public Facility create(final Facility entity) {
//        if (entity == null) {
//            throw new RuntimeException("Facility is required!");
//        }
//
//        entity.setAddress(entity.getAddress() != null && StringUtils.hasText(entity.getAddress().getId())
//                ? addressService.findById(entity.getAddress().getId())
//                : null);
//
//        entity.setContractor(entity.getContractor() != null && StringUtils.hasText(entity.getContractor().getId())
//                ? contractorService.findById(entity.getContractor().getId())
//                : null);
//
//        entity.setRegister(entity.getRegister() != null && StringUtils.hasText(entity.getRegister().getCode())
//                ? classifierService.findByCode(entity.getRegister().getCode())
//                : null);
//
//        if (!CollectionUtils.isEmpty(entity.getFacilityCapacities())) {
//            List<FacilityCapacity> facilityCapacities = new ArrayList<>();
//            entity.getFacilityCapacities().forEach(fc ->
//                    facilityCapacities.add(FacilityCapacity.builder()
//                            .product(fc.getProduct())
//                            .quantity(fc.getQuantity())
//                            .material(nomenclatureService.findByCode(fc.getMaterial().getCode()))
//                            .unit(nomenclatureService.findByCode(fc.getUnit().getCode()))
//                            .build()));
//            entity.getFacilityCapacities().clear();
//            entity.getFacilityCapacities().addAll(facilityCapacities);
//        }
//
//        return repository.save(entity);
//    }

    @Transactional
    public Facility update(final Long id, final FacilityDTO dto, final Language language) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID field is required");
        }
        if (!id.equals(dto.getId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }

        Facility current = findById(id);

        current.setMail(dto.getMail());
        current.setPhone1(dto.getPhone1());
        current.setPhone2(dto.getPhone2());
        current.setEnabled(dto.getEnabled());

        current.setAddress(dto.getAddress().getId() != null && dto.getAddress().getId() != null && dto.getAddress().getId() > 0
                ? addressService.findById(dto.getAddress().getId()) : null);
// TODO REFACTOR the facility has more than one contractors and one of them is the owner
//        current.setContractor(dto.getContractorId() != null && StringUtils.hasText(dto.getContractorId())
//                ? contractorService.findById(dto.getContractorId()) : null);

        FacilityI18n i18n = current.getI18n(language);
        i18n.setName(dto.getName());
        i18n.setDescription(dto.getDescription());

        for (KeyValueDTO r : dto.getRegisters()) {
            Classifier register = classifierService.findByCode(r.getCode());
            if (register != null) {
                if (current.getRegisters().stream().noneMatch(c -> register.getCode().equals(c.getCode()))) {
                    current.getRegisters().add(register);
                }
            }
        }

        current.getFacilityCapacities().clear();
        if (!CollectionUtils.isEmpty(dto.getFacilityCapacities())) {
            List<FacilityCapacity> facilityCapacities = new ArrayList<>();
            dto.getFacilityCapacities().forEach(fc ->
                    facilityCapacities.add(FacilityCapacity.builder()
                            .product(fc.getProduct())
                            .quantity(fc.getQuantity())
                            .material(nomenclatureService.findByCode(fc.getMaterialCode()))
                            .unit(nomenclatureService.findByCode(fc.getUnitCode()))
                            .build()));
            current.getFacilityCapacities().addAll(facilityCapacities);
        }
        if (StringUtils.hasText(dto.getWaterSupplyTypeCode())) {
            Nomenclature waterSupplyType = nomenclatureService.findByCode(dto.getWaterSupplyTypeCode());
            current.setWaterSupplyType(waterSupplyType);
        }
        if (StringUtils.hasText(dto.getActivityTypeCode())) {
            Nomenclature activityType = nomenclatureService.findByCode(dto.getActivityTypeCode());
            current.setActivityType(activityType);
        }

        return repository.save(current);
    }

    @Transactional
    public FacilityDTO changeFacilityOwner(final Long facilityId, final Long currentOwnerId, final Long newOwnerId,
                                           final String comment, final Language language) {
        if (currentOwnerId == null || currentOwnerId <= 0) {
            throw new RuntimeException("ID field is required");
        }
        if (facilityId == null || facilityId <= 0) {
            throw new RuntimeException("facilityId field is required");
        }
        if (newOwnerId == null || newOwnerId <= 0) {
            throw new RuntimeException("newOwnerId is required");
        }
        if (currentOwnerId.equals(newOwnerId)) {
            throw new RuntimeException("Path variable id equals to the path variable newFacilitiesOwnerId");
        }

        Contractor current = contractorService.findById(currentOwnerId);

        Facility facility = findById(facilityId);

        if (CollectionUtils.isEmpty(current.getContractorFacilities())) {
            throw new RuntimeException("Contractor with ID: " + currentOwnerId + " Doesn't have facility/ies");
        }

        Contractor newFacilitiesOwner = contractorService.findById(newOwnerId);

        current.getContractorFacilities().stream()
                .filter(cf -> currentOwnerId.equals(cf.getContractor().getId()) && facilityId.equals(cf.getFacility().getId()) && cf.getOwner())
                .findAny().ifPresent(c -> c.setOwner(false));

        // TODO Add ServiceType to method parameters
        current.getContractorFacilities().stream()
                .filter(cf -> newOwnerId.equals(cf.getContractor().getId()) && facilityId.equals(cf.getFacility().getId()))
                .findAny().ifPresentOrElse(
                        c -> c.setOwner(true),
                        () -> newFacilitiesOwner.addContractorFacility(facility, null, true)
                );

        return FacilityDTO.of(repository.save(facility), language);
    }

    @Transactional(readOnly = true)
    public List<FacilityDTO> findRevisions(final Long id, Language language) {
        List<FacilityDTO> revisions = new ArrayList<>();
        repository.findRevisions(id).get().forEach(r -> {
            FacilityDTO rev = FacilityDTO.of(r.getEntity(), language);
            rev.setRevisionMetadata(RevisionMetadataDTO.builder()
                    .revisionNumber(r.getMetadata().getRevisionNumber().orElse(null))
                    .revisionInstant(r.getMetadata().getRevisionInstant().orElse(null))
                    .revisionType(r.getMetadata().getRevisionType().name())
                    .createdBy(r.getEntity().getCreatedBy())
                    .createdDate(r.getEntity().getCreatedDate())
                    .lastModifiedBy(r.getEntity().getLastModifiedBy())
                    .lastModifiedDate(r.getEntity().getLastModifiedDate())
                    .build()
            );
            revisions.add(rev);
        });
        return revisions;
    }

    @Transactional(readOnly = true)
    public Facility findByRegNumber(final String regNumber) {
        return repository.findByRegNumber(regNumber)
                .orElseThrow(() -> new EntityNotFoundException(Facility.class, regNumber));
    }

//    @Transactional(readOnly = true)
//    public Page<FacilityVO> findByRegisterCodeAndBranchId(final String username, final String registerCode, final Language language, final Pageable pageable) {
//        User currentUser = userService.findByUsername(username);
//        if (currentUser.getBranch() == null || !StringUtils.hasText(currentUser.getBranch().getId())) {
//            throw new RuntimeException("The current user has no branch");
//        }
//        String branchId = currentUser.getBranch().getId();
//
//        return repository.findByRegister_CodeAndBranch_Id(registerCode, branchId, language.getLanguageId(), pageable);
//    }

    @Transactional(readOnly = true)
    public List<FacilityDTO> findAllByContractorId(final Long contractorId, final Language language) {
        return FacilityDTO.of(contractorFacilityRepository.findAllByContractorIdAndFacilityStatus(contractorId, FacilityStatus.ACTIVE)
                .stream().map(ContractorFacility::getFacility).collect(Collectors.toList()), language);
    }

    @Transactional(readOnly = true)
    public List<FacilityDTO> findAllByContractorIdAndServiceType(final Long contractorId, final String serviceType, final Language language) {
        return FacilityDTO.of(repository.findAllByContractorIdAndServiceTypeAndStatus(
                contractorId, ServiceType.valueOf(serviceType), FacilityStatus.ACTIVE, language.getLanguageId()), language);
    }

//    @Transactional(readOnly = true)
//    public Page<FacilityVO> findByDivisionCodeAndBranchId(final String username, final String divisionCode, final Language language, final Pageable pageable) {
//        User currentUser = userService.findByUsername(username);
//        if (currentUser.getBranch() == null || !StringUtils.hasText(currentUser.getBranch().getId())) {
//            throw new RuntimeException("The current user has no branch");
//        }
//        String branchId = currentUser.getBranch().getId();
//
//        return repository.findByDivisionCodeAndBranch(divisionCode, branchId, language.getLanguageId(), pageable);
//    }

    public Facility findByFacilityPaperNumberOrNull(final String facilityPaperNumber, final Language language) {
        return repository.findByFacilityPaperNumber(facilityPaperNumber, language.getLanguageId()).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<VehicleDTO> findAllVehicles(Long id, Language language) {
        return VehicleDTO.of(findById(id).getVehicles(), language);
    }

    @Transactional
    public InspectionDTO createInspection(final InspectionDTO dto, final Language language) {
        if (dto == null) {
            throw new RuntimeException("Inspection cannot be null.");
        }
        if (dto.getInspectionType() == null || InspectionType.FOR_APPROVAL.equals(dto.getInspectionType())) {
            throw new RuntimeException("Wrong inspection type: " + dto.getInspectionType());
        }
        if (dto.getContractorId() == null || dto.getContractorId() <= 0) {
            throw new RuntimeException("Contractor ID cannot be null.");
        }

        Facility facility = findById(dto.getFacilityId());
        Inspection inspection = InspectionDTO.to(dto, language);
        inspection.setFacility(facility);
        inspection.setStatus(InspectionStatus.PROCESSING);

        if (!CollectionUtils.isEmpty(dto.getUsers())) {
            dto.getUsers().forEach(id -> inspection.getUsers().add(userService.findById(id)));
        }
        if (dto.getRecordId() != null && dto.getRecordId() > 0) {
            inspection.setRecord(recordService.findById(dto.getRecordId()));
        }
        if (!CollectionUtils.isEmpty(dto.getReasonsCodes())) {
            dto.getReasonsCodes().forEach(rc ->
                    inspection.getReasons().add(nomenclatureService.findByCode(rc)));
        }
        if (dto.getContractorId() != null && dto.getContractorId() > 0) {
            inspection.setContractor(contractorService.findById(dto.getContractorId()));
        }

        return InspectionDTO.of(inspectionService.create(inspection), language);
    }

    @Transactional
    public InspectionDTO completeInspection(final Long id, final InspectionDTO dto, final Language language) {
        if (dto == null) {
            throw new RuntimeException("Inspection DTO cannot be null.");
        }
        if (!id.equals(dto.getFacilityId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter facilityId");
        }
        if (dto.getInspectionType() == null || InspectionType.FOR_APPROVAL.equals(dto.getInspectionType())) {
            throw new RuntimeException("Wrong inspection type: " + dto.getInspectionType());
        }

        Facility facility = findById(dto.getFacilityId());

        Inspection inspection = facility.getInspections().stream()
                .filter(i -> i.getId().equals(dto.getId()))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException(Inspection.class, dto.getId()));

        if (InspectionStatus.COMPLETED.equals(inspection.getStatus())) {
            throw new RuntimeException("Inspection is already completed.");
        }

        inspection.getI18n(language).setDescription(dto.getDescription());
        inspection.setEndDate(dto.getEndDate());
        inspection.setStatus(InspectionStatus.COMPLETED);
        if (dto.getRiskLevel() != null) {
            inspection.setRiskLevel(dto.getRiskLevel());
        }
        inspection.getReasons().clear();
        if (!CollectionUtils.isEmpty(dto.getReasonsCodes())) {
            dto.getReasonsCodes().forEach(code -> inspection.getReasons().add(nomenclatureService.findByCode(code)));
        }
        inspection.getUsers().clear();
        if (!CollectionUtils.isEmpty(dto.getUsers())) {
            dto.getUsers().forEach(userId -> inspection.getUsers().add(userService.findById(userId)));
        }

        facility.setStatus(FacilityStatus.ACTIVE);
//        facility.setEnabled(true);

        repository.save(facility);

        return InspectionDTO.of(inspection, language);
    }

    @Transactional
    public FacilityDTO updateStatus(final Long id, final KeyValueDTO dto, final Language language) {
        if (id == null || id <= 0) {
            throw new RuntimeException("ID field is required");
        }
        if (!id.equals(dto.getId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }

        Facility current = findById(id);
        current.setStatus(FacilityStatus.valueOf(dto.getStatus()));
        return FacilityDTO.of(repository.save(current), language);
    }
}
