package bg.bulsi.bfsa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class CropDoseBO {
    private String applicationCode;
    private String applicationName;
    private Double doseMin;
    private Double doseMax;
    private String doseUnitCode;
    private String doseUnitName;
    private Integer treatmentMin;
    private Integer treatmentMax;
    private Double mixtureConcentrationMin;
    private Double mixtureConcentrationMax;
    private String concentrationDoseUnitCode;
    private String concentrationDoseUnitName;
    private String deadline;
}

