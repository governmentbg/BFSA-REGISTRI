package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.FoodSupplementStatus;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.FoodSupplement;
import bg.bulsi.bfsa.model.FoodSupplementI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
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
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodSupplementDTO {

    private Long id;
    private String regNumber;
    private LocalDate regDate;
    private String notificationNumber;
    private LocalDate notificationDate;
    private String name;
    private String purpose;
    private String ingredients;
    private String description;
    private LocalDate marketReleaseDate;
    private String deletionOrderNumber;
    private LocalDate deletionOrderDate;
    private Boolean enabled;
    private Long applicantId;
    private Double quantity;
    private String manufactureCompanyName;
    private String distributionFacilityAddress;
    private String manufactureFacilityAddress;
    private String measuringUnitCode;
    private String measuringUnitName;
    private String facilityTypeCode;
    private String facilityTypeName;
    private String foodSupplementTypeCode;
    private String foodSupplementTypeName;
    private List<KeyValueDTO> countries = new ArrayList<>();
    private FoodSupplementStatus status;
    private RevisionMetadataDTO revisionMetadata;

    public static FoodSupplement to(final FoodSupplementDTO source, final Language language) {
        FoodSupplement entity = new FoodSupplement();
        BeanUtils.copyProperties(source, entity);

        if (source.getApplicantId() != null && source.getApplicantId() > 0) {
            entity.setApplicant(Contractor.builder().id(source.getApplicantId()).build());
        }

        entity.getI18ns().add(new FoodSupplementI18n(source.getName(), source.getPurpose(), source.getIngredients(),
                source.getDescription(), source.manufactureCompanyName, entity, language));

        if (!CollectionUtils.isEmpty(source.getCountries())) {
            source.getCountries().forEach(cc -> entity.getCountries().add(
                    Country.builder().code(cc.getCode()).build()
            ));
        }
        entity.setFoodSupplementType(Nomenclature.builder().code(source.getFoodSupplementTypeCode()).build());
        entity.setMeasuringUnit(StringUtils.hasText(source.getMeasuringUnitCode())
                ? Nomenclature.builder().code(source.getMeasuringUnitCode()).build()
                : null);

        return entity;
    }

    public static FoodSupplementDTO of(final FoodSupplement source, final Language language) {
        FoodSupplementDTO dto = new FoodSupplementDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getApplicant() != null) {
            dto.setApplicantId(source.getApplicant().getId());
        }
        if (!CollectionUtils.isEmpty(source.getCountries())) {
            source.getCountries().forEach(c -> dto.getCountries().add(KeyValueDTO.builder()
                    .code(c.getCode()).name(c.getI18n(language).getName()).build()));
        }

        if(source.getMeasuringUnit() != null) {
            dto.setMeasuringUnitCode(source.getMeasuringUnit().getCode());
            dto.setMeasuringUnitName(source.getMeasuringUnit().getI18n(language).getName());
        }
        if (source.getFoodSupplementType() != null) {
            dto.setFoodSupplementTypeCode(source.getFoodSupplementType().getCode());
            dto.setFoodSupplementTypeName(source.getFoodSupplementType().getI18n(language).getName());
        }
        if (source.getFacilityType() != null) {
            dto.setFacilityTypeCode(source.getFacilityType().getCode());
            dto.setFacilityTypeName(source.getFacilityType().getI18n(language).getName());
        }

        FoodSupplementI18n i18n = source.getI18n(language);
        dto.setName(i18n.getName());
        dto.setPurpose(i18n.getPurpose());
        dto.setIngredients(i18n.getIngredients());
        dto.setDescription(i18n.getDescription());
        return dto;
    }

    public static List<FoodSupplementDTO> of(final List<FoodSupplement> sources, final Language language) {
        return sources.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }
}
