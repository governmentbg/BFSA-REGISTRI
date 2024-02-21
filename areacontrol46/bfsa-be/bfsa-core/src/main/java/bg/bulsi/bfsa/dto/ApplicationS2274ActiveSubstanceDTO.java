package bg.bulsi.bfsa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2274ActiveSubstanceDTO {
    private String activeSubstanceCode;
    private String activeSubstanceName;
    private String manufacturer;
    private String manufacturerLat;
    private String manufacturerCountryCode;
    private String manufacturerCountryName;
    private Double substanceQuantity;
    private String substanceQuantityUnitCode;
    private String substanceQuantityUnitName;

    @Builder.Default
    private List<KeyValueDTO> pppActiveSubstanceTypes = new ArrayList<>();
}
