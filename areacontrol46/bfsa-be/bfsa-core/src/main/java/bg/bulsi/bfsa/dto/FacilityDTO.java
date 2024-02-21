package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.model.Address;
import bg.bulsi.bfsa.model.BaseApprovalDocument;
import bg.bulsi.bfsa.model.BaseEntity;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.FacilityI18n;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FacilityDTO {
    private String identifier;
    private Long id;
    private String name;
    private String description;
    private Boolean enabled;
    private Long contractorId;
    private String foodBankLicenseId;
    private String regNumber;
    private LocalDate regDate;
    private Long branchId;
    private String activityTypeCode;
    private String activityTypeName;
    private String facilityTypeCode;
    private String facilityTypeName;
    private String waterSupplyTypeCode;
    private String waterSupplyTypeName;
    private String activityDescription;
    private String disposalWasteWater;
    private String permission177;
    private Double capacity;
    private Double area;
    private String measuringUnitCode;
    private String measuringUnitName;
    private String subActivityTypeCode;
    private String subActivityTypeName;
    private String rawMilkTypeCode;
    private Double fridgeCapacity;
    private String foodTypeDescription;
    private String periodCode;
    private String periodName;
    private AddressDTO address;
    private RevisionMetadataDTO revisionMetadata;

    private String mail;
    private String phone1;
    private String phone2;
    private FacilityStatus status;

    private String facilityPaperRegNumbers;

    @Builder.Default
    private List<KeyValueDTO> activityTypes = new ArrayList<>();
    @Builder.Default
    private List<FacilityCapacityDTO> facilityCapacities = new ArrayList<>();
    @Builder.Default
    private Set<KeyValueDTO> relatedActivityCategories = new HashSet<>();
    @Builder.Default
    private Set<KeyValueDTO> associatedActivityCategories = new HashSet<>();
    @Builder.Default
    private Set<KeyValueDTO> animalSpecies = new HashSet<>();
    @Builder.Default
    private Set<KeyValueDTO> remarks = new HashSet<>();
    @Builder.Default
    private Set<KeyValueDTO> pictograms = new HashSet<>();
    @Builder.Default
    private List<KeyValueDTO> registers = new ArrayList<>();
    //    TODO: Add field where is it necessary
    private List<Long> inspectionIds;
    @Builder.Default
    private Set<KeyValueDTO> foodTypes = new HashSet<>();

    public static Facility to(final FacilityDTO source, Language language) {

        Facility entity = new Facility();
        BeanUtils.copyProperties(source, entity);

        entity.setAddress(source.getAddress().getId() != null && source.getAddress().getId() > 0
                ? Address.builder().id(source.getAddress().getId()).build()
                : null);
        entity.setFacilityType(StringUtils.hasText(source.getFacilityTypeCode())
                ? Nomenclature.builder().code(source.getFacilityTypeCode()).build()
                : null);
        entity.setSubActivityType(StringUtils.hasText(source.getSubActivityTypeCode())
                ? Nomenclature.builder().code(source.getSubActivityTypeCode()).build()
                : null);

        if (!CollectionUtils.isEmpty(source.getRelatedActivityCategories())) {
            source.getRelatedActivityCategories().forEach(
                    c -> entity.getRelatedActivityCategories().add(Nomenclature.builder().code(c.getCode()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getAssociatedActivityCategories())) {
            source.getAssociatedActivityCategories().forEach(
                    c -> entity.getAssociatedActivityCategories().add(Nomenclature.builder().code(c.getCode()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getAnimalSpecies())) {
            source.getAnimalSpecies().forEach(
                    c -> entity.getAnimalSpecies().add(Nomenclature.builder().code(c.getCode()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getRemarks())) {
            source.getRemarks().forEach(
                    c -> entity.getRemarks().add(Nomenclature.builder().code(c.getCode()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getPictograms())) {
            source.getPictograms().forEach(
                    c -> entity.getPictograms().add(Nomenclature.builder().code(c.getCode()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getInspectionIds())) {
            source.getInspectionIds().forEach(
                    c -> entity.getInspections().add(Inspection.builder().facility(entity).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getFoodTypes())) {
            source.getFoodTypes().forEach(
                    c -> entity.getFoodTypes().add(Classifier.builder().code(c.getCode()).build())
            );
        }
        entity.getI18ns().add(new FacilityI18n(
                source.name, source.description,
                source.activityDescription, source.permission177,
                source.disposalWasteWater, source.foodTypeDescription,
                entity, language));

        entity.setBranch(source.branchId != null && source.branchId > 0
                ? Branch.builder().id(source.getBranchId()).build()
                : null);

        entity.setActivityType(StringUtils.hasText(source.getActivityTypeCode())
                ? Nomenclature.builder().code(source.getActivityTypeCode()).build()
                : null);

        return entity;
    }

    public static List<Facility> to(final List<FacilityDTO> source, Language language) {
        return source.stream().map(d -> to(d, language)).collect(Collectors.toList());
    }

    public static FacilityDTO of(final Facility source, final Language language) {
        FacilityDTO dto = baseOf(source, language);

        // TODO: Какво правим ако един обект има повече от 1 собственик??? О_o
//        if (!CollectionUtils.isEmpty(source.getContractorFacilities())) {
//            source.getContractorFacilities().stream()
//                    .filter(cf -> cf.getOwner() != null)
//                    .findAny()
//                    .ifPresent(cf -> dto.setContractorId(cf.getContractor().getId()));
//        }

        if (source.getActivityType() != null) {
            dto.setActivityTypeCode(source.getActivityType().getCode());
            dto.setActivityTypeName(source.getActivityType().getI18n(language).getName());
        }
        if (source.getSubActivityType() != null) {
            dto.setSubActivityTypeCode(source.getSubActivityType().getCode());
            dto.setSubActivityTypeName(source.getSubActivityType().getI18n(language).getName());
        }
        if (source.getFacilityType() != null) {
            dto.setFacilityTypeCode(source.getFacilityType().getCode());
            dto.setFacilityTypeName(source.getFacilityType().getI18n(language).getName());
        }
        if (source.getWaterSupplyType() != null) {
            dto.setWaterSupplyTypeCode(source.getWaterSupplyType().getCode());
            dto.setWaterSupplyTypeName(source.getWaterSupplyType().getI18n(language).getName());
        }
        if (source.getMeasuringUnit() != null) {
            dto.setMeasuringUnitName(source.getMeasuringUnit().getI18n(language).getName());
        }
        if (source.getPeriod() != null) {
            dto.setPeriodName(source.getPeriod().getI18n(language).getName());
        }
        if (!CollectionUtils.isEmpty(source.getRelatedActivityCategories())) {
            source.getRelatedActivityCategories().forEach(
                    e -> dto.getRelatedActivityCategories().add(KeyValueDTO.builder()
                            .code(e.getCode()).name(e.getI18n(language).getName()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getAssociatedActivityCategories())) {
            source.getAssociatedActivityCategories().forEach(
                    e -> dto.getAssociatedActivityCategories().add(KeyValueDTO.builder()
                            .code(e.getCode()).name(e.getI18n(language).getName()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getAnimalSpecies())) {
            source.getAnimalSpecies().forEach(
                    e -> dto.getAnimalSpecies().add(KeyValueDTO.builder()
                            .code(e.getCode()).name(e.getI18n(language).getName()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getRemarks())) {
            source.getRemarks().forEach(
                    e -> dto.getRemarks().add(KeyValueDTO.builder()
                            .code(e.getCode()).name(e.getI18n(language).getName()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getPictograms())) {
            source.getPictograms().forEach(
                    e -> dto.getPictograms().add(KeyValueDTO.builder()
                            .code(e.getCode()).name(e.getI18n(language).getName()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getFoodTypes())) {
            dto.setFoodTypes(KeyValueDTO.ofClassifiers(source.getFoodTypes(), language));
            source.getFoodTypes().forEach(
                    ft -> dto.getFoodTypes().add(KeyValueDTO.builder()
                            .code(ft.getCode()).name(ft.getI18n(language).getName()).build())
            );
        }

        if (!CollectionUtils.isEmpty(source.getRegisters())) {
            for (Classifier c : source.getRegisters()) {
                dto.getRegisters().add(KeyValueDTO.builder().code(c.getCode()).value(c.getI18n(language).getName()).build());
            }
        }

        if (!CollectionUtils.isEmpty(source.getInspections())) {
            dto.setInspectionIds(source.getInspections()
                    .stream()
                    .map(BaseEntity::getId)
                    .collect(Collectors.toList()));
        }
        if (source.getBranch() != null) {
            dto.setBranchId(source.getBranch().getId());
        }
        if (!CollectionUtils.isEmpty(source.getFacilityCapacities())) {
            source.getFacilityCapacities().forEach(
                    fc -> dto.getFacilityCapacities()
                            .add(FacilityCapacityDTO.builder()
                                    .product(StringUtils.hasText(fc.getProduct()) ? fc.getProduct() : null)
                                    .quantity(fc.getQuantity() != null ? fc.getQuantity() : null)
                                    .materialCode(fc.getMaterial() != null && StringUtils.hasText(fc.getMaterial().getCode())
                                            ? fc.getMaterial().getCode()
                                            : null)
                                    .unitCode(fc.getUnit() != null && StringUtils.hasText(fc.getUnit().getCode())
                                            ? fc.getUnit().getCode()
                                            : null)
                                    .fridgeCapacity(fc.getFridgeCapacity() != null
                                            ? fc.getFridgeCapacity()
                                            : null)
                                    .rawMilkTypeCode(fc.getRawMilkType() != null && StringUtils.hasText(fc.getRawMilkType().getCode())
                                            ? fc.getRawMilkType().getCode()
                                            : null)
                                    .build())
            );
        }
        if (source.getPeriod() != null) {
            dto.setPeriodCode(source.getPeriod().getCode());
        }
        if (source.getMeasuringUnit() != null) {
            dto.setMeasuringUnitCode(source.getMeasuringUnit().getCode());
            dto.setMeasuringUnitName(source.getMeasuringUnit().getI18n(language).getName());
        }
        if (!CollectionUtils.isEmpty(source.getFacilityPapers())) {
            dto.setFacilityPaperRegNumbers(source.getFacilityPapers()
                    .stream().map(BaseApprovalDocument::getRegNumber).collect(Collectors.joining(", ")));
        }

        return dto;
    }

    public static FacilityDTO baseOf(final Facility source, final Language language) {
        FacilityDTO dto = new FacilityDTO();
        BeanUtils.copyProperties(source, dto);

        FacilityI18n i18n = source.getI18n(language);
        if (i18n != null) {
            dto.setName(i18n.getName());
            dto.setDescription(i18n.getDescription());
            dto.setActivityDescription(i18n.getActivityDescription());
            dto.setPermission177(i18n.getPermission177());
            dto.setDisposalWasteWater(i18n.getDisposalWasteWater());
            dto.setFoodTypeDescription(i18n.getFoodTypeDescription());
        }

        if (source.getAddress() != null) {
            dto.setAddress(AddressDTO.of(source.getAddress(), language));
        }

        return dto;
    }

    public static List<FacilityDTO> baseOf(final List<Facility> source, final Language language) {
        return source.stream().map(e -> baseOf(e, language)).collect(Collectors.toList());
    }

    public static List<FacilityDTO> of(final List<Facility> source, final Language language) {
        return source.stream().map(e -> of(e, language)).collect(Collectors.toList());
    }
}
