package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.PlaceType;
import bg.bulsi.bfsa.enums.TSB;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.Settlement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SettlementDTO {

    @NotNull
    private String code;
    private String name;
    private String nameLat;
    private String regionCode;
    private String regionName;
    private String regionNameLat;
    private String municipalityCode;
    private String municipalityName;
    private String municipalityNameLat;
    private String placeType;
    private String tsb;
    private String parentCode;
    private String countryCode;
    private Boolean enabled;
    private RevisionMetadataDTO revisionMetadata;

    @Builder.Default
    private List<SettlementDTO> subSettlements = new ArrayList<>();

    public SettlementDTO(Settlement settlement) {
        this.code = settlement.getCode();
        this.name = settlement.getName();
        this.nameLat = settlement.getNameLat();
        this.regionCode = settlement.getRegionCode();
        this.regionName = settlement.getRegionName();
        this.regionNameLat = settlement.getRegionNameLat();
        this.municipalityCode = settlement.getMunicipalityCode();
        this.municipalityName = settlement.getMunicipalityName();
        this.municipalityNameLat = settlement.getMunicipalityNameLat();
        if (settlement.getPlaceType() != null) {
            this.placeType = settlement.getPlaceType().name();
        }
        if (settlement.getTsb() != null) {
            this.tsb = settlement.getTsb().name();
        }
        this.parentCode = settlement.getParentCode();
        this.countryCode = settlement.getCode();
        this.enabled = settlement.getEnabled();
    }

    public static Settlement to(final SettlementDTO dto) {
        return to(dto, null);
    }

    public static Settlement to(final SettlementDTO source, final Settlement parent) {
        Settlement entity = new Settlement();
        entity.setCode(source.getCode());
        entity.setPlaceType(StringUtils.hasText(source.getPlaceType()) ? PlaceType.valueOf(source.getPlaceType()) : null);
        entity.setTsb(StringUtils.hasText(source.getTsb()) ? TSB.valueOf(source.getTsb()) : null);
        entity.setRegionCode(source.getRegionCode());
        entity.setRegionName(source.getRegionName());
        entity.setRegionNameLat(source.getRegionNameLat());
        entity.setName(source.getName());
        entity.setNameLat(source.getNameLat());
        entity.setMunicipalityCode(source.getMunicipalityCode());
        entity.setMunicipalityName(source.getMunicipalityName());
        entity.setMunicipalityNameLat(source.getMunicipalityNameLat());
        entity.setEnabled(source.getEnabled());
        entity.setParentCode(parent != null && StringUtils.hasText(parent.getCode()) ? parent.getCode() : null);
        entity.setCountry(StringUtils.hasText(source.getCountryCode()) ? Country.builder().code(source.getCountryCode()).build() : null);

        if (!CollectionUtils.isEmpty(source.getSubSettlements())) {
            entity.getSubSettlements().addAll(to(source.getSubSettlements(), entity));
        }

        return entity;
    }

    public static List<Settlement> to(final List<SettlementDTO> source, final Settlement parent) {
        return source.stream().map(d -> to(d, parent)).collect(Collectors.toList());
    }

    public static SettlementDTO of(final Settlement source) {
        SettlementDTO dto = new SettlementDTO();
        dto.setCode(source.getCode());
        dto.setEnabled(source.getEnabled());
        dto.setPlaceType(source.getPlaceType() != null ? source.getPlaceType().name() : null);
        dto.setTsb(source.getTsb() != null ? source.getTsb().name() : null);
        dto.setRegionCode(source.getRegionCode());
        dto .setRegionName(source.getRegionName());
        dto.setRegionNameLat(source.getRegionNameLat());
        dto.setName(source.getName());
        dto.setNameLat(source.getNameLat());
        dto.setMunicipalityCode(source.getMunicipalityCode());
        dto.setMunicipalityName(source.getMunicipalityName());
        dto.setMunicipalityNameLat(source.getMunicipalityNameLat());
        dto.setParentCode(source.getParentCode());
        dto.setCountryCode(source.getCountry() != null && StringUtils.hasText(source.getCountry().getCode())
                ? source.getCountry().getCode() : null);

        if (!CollectionUtils.isEmpty(source.getSubSettlements())) {
            dto.getSubSettlements().addAll(of(source.getSubSettlements()));
        }

        return dto;
    }

    public static List<SettlementDTO> of(final List<Settlement> source) {
        return source.stream().map(e -> of(e)).collect(Collectors.toList());
    }
}
