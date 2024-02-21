package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.FertilizerCert;
import bg.bulsi.bfsa.model.FertilizerCertI18n;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FertilizerCertDTO {
    private Long id;
    private String regNumber;
    private LocalDate regDate;
    private LocalDate entryDate;
    private LocalDate validUntilDate;
    private String edition;
    private Double ph;
    private Double dose;
    private Double waterAmount;
    private Integer appNumber;
    private String orderNumber;
    private LocalDate orderDate;
    private Boolean enabled;
    private String fertilizerTypeCode;
    private Long manufacturerId;
    private Long certificateHolderId;
    private String name;
    private String ingredients;
    private String wording;
    private String crop;
    private String application;
    private String reason;
    private String description;
    private RevisionMetadataDTO revisionMetadata;

    public static FertilizerCert to(final FertilizerCertDTO source, final Language language) {
        FertilizerCert entity = new FertilizerCert();
        BeanUtils.copyProperties(source, entity);

        if (StringUtils.hasText(source.getFertilizerTypeCode())) {
            entity.setFertilizerType(Nomenclature.builder().code(source.getFertilizerTypeCode()).build());
        }
        if (source.getManufacturerId() != null && source.getManufacturerId() > 0) {
            entity.setManufacturer(Contractor.builder().id(source.getManufacturerId()).build());
        }
        if (source.getCertificateHolderId() != null && source.getCertificateHolderId() > 0) {
            entity.setCertificateHolder(Contractor.builder().id(source.getCertificateHolderId()).build());
        }
        entity.getI18ns().add(new FertilizerCertI18n(
                source.getName(), source.getIngredients(), source.getWording(),
                source.getCrop(), source.getApplication(), source.getReason(),
                source.getDescription(), entity, language));

        return entity;
    }

    public static List<FertilizerCert> to(final List<FertilizerCertDTO> source, final Language language) {
        return source.stream().map(d -> to(d, language)).collect(Collectors.toList());
    }
    public static FertilizerCertDTO of(final FertilizerCert source, final Language language) {
        FertilizerCertDTO dto = new FertilizerCertDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getManufacturer() != null) {
            dto.setManufacturerId(source.getManufacturer().getId());
        }
        if (source.getFertilizerType() != null) {
            dto.setFertilizerTypeCode(source.getFertilizerType().getCode());
        }
        if (source.getCertificateHolder() != null) {
            dto.setCertificateHolderId(source.getCertificateHolder().getId());
        }

        FertilizerCertI18n i18n = source.getI18n(language);
        dto.setName(i18n.getName());
        dto.setIngredients(i18n.getIngredients());
        dto.setWording(i18n.getWording());
        dto.setCrop(i18n.getCrop());
        dto.setApplication(i18n.getApplication());
        dto.setReason(i18n.getReason());
        dto.setDescription(i18n.getDescription());

        return dto;
    }

    public static List<FertilizerCertDTO> of(final List<FertilizerCert> sources, final Language language) {
        return sources.stream().map(f -> of(f, language)).collect(Collectors.toList());
    }
}
