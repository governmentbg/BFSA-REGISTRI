package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.PlaceType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class SettlementVO {
    private String id;
    private String name;
    private String nameLat;
    private PlaceType placeType;
    private String municipalityName;
    private String municipalityNameLat;
    private String municipalityCode;
    private String regionName;
    private String regionNameLat;
    private String regionCode;
    private String parentCode;
    private String settlementCode;
    private String resortCode;

    public SettlementVO(final String id,
                        final String name,
                        final String nameLat) {
        this.id = id;
        this.name = name;
        this.nameLat = nameLat;
    }

    public SettlementVO(final String regionCode,
                        final String municipalityCode,
                        final String settlementCode,
                        final String resortCode) {
        this.regionCode = regionCode;
        this.municipalityCode = municipalityCode;
        this.settlementCode = settlementCode;
        this.resortCode = resortCode;
    }

    public SettlementVO(final String id,
                        final String name,
                        final String nameLat,
                        final String municipalityName,
                        final String municipalityNameLat,
                        final String regionName,
                        final String regionNameLat,
                        final PlaceType placeType) {
        this.id = id;
        this.name = name;
        this.nameLat = nameLat;
        this.municipalityName = municipalityName;
        this.municipalityNameLat = municipalityNameLat;
        this.regionName = regionName;
        this.regionNameLat = regionNameLat;
        this.placeType = placeType;
    }
}
