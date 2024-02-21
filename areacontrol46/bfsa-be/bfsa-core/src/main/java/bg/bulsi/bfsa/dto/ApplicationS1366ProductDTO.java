package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.ApplicationStatus;
import bg.bulsi.bfsa.model.ApplicationS1366Product;
import bg.bulsi.bfsa.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
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
public class ApplicationS1366ProductDTO {
    // Common
    private Long id;
    private Set<KeyValueDTO> foodTypes = new HashSet<>();
    private String productName;
    private String productTrademark;
    private String productCountryCode;
    private String productCountryName;
    // Veterinary
    private Double productTotalNetWeight;
    private String productTotalNetWeightUnitCode;
    private String productTotalNetWeightUnitName;
    // Health
    private String productPackageType;
    private OffsetDateTime productExpiryDate;
    private OffsetDateTime productManufactureDate;

    private List<ApplicationS1366ProductBatchDTO> batches = new ArrayList<>();

    public static ApplicationS1366Product to(final ApplicationS1366ProductDTO source) {
        ApplicationS1366Product entity = new ApplicationS1366Product();
        BeanUtils.copyProperties(source, entity);

        return entity;
    }

    public static List<ApplicationS1366Product> to(final List<ApplicationS1366ProductDTO> source) {
        return source.stream().map(ApplicationS1366ProductDTO::to).collect(Collectors.toList());
    }

    public static ApplicationS1366ProductDTO of(final ApplicationS1366Product source, final ApplicationStatus status, final Language language) {
        ApplicationS1366ProductDTO dto = new ApplicationS1366ProductDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getProductCountry() != null) {
            dto.setProductCountryCode(source.getProductCountry().getCode());
            dto.setProductCountryName(source.getProductCountry().getI18n(language).getName());
        }

        if (source.getProductTotalNetWeightUnit() != null) {
            dto.setProductTotalNetWeightUnitCode(source.getProductTotalNetWeightUnit().getCode());
            dto.setProductTotalNetWeightUnitName(source.getProductTotalNetWeightUnit().getI18n(language).getName());
        }

        if (!CollectionUtils.isEmpty(source.getApplicationS1366ProductBatches())) {
            dto.setBatches(ApplicationS1366ProductBatchDTO.of(source.getApplicationS1366ProductBatches(), language));
        }

        if (ApplicationStatus.ENTERED.equals(status)) {
            dto.setFoodTypes(source.getApplicationS1366ProductFoodTypes());
        } else if (!CollectionUtils.isEmpty(source.getFoodTypes())) {
            dto.setFoodTypes(KeyValueDTO.ofClassifiers(source.getFoodTypes(), language));
        }

        return dto;
    }

    public static List<ApplicationS1366ProductDTO> of(final List<ApplicationS1366Product> source, final ApplicationStatus status, final Language language) {
        return source.stream().map(r -> of(r, status, language)).collect(Collectors.toList());
    }
}
