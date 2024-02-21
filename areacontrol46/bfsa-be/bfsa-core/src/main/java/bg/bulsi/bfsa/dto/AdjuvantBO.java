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
public class AdjuvantBO {
    private String ingredientCasNumber;
    private String ingredientChemName;
    private String ingredientContent;
    private String ingredientUnitTypeCode; // Nomenclature
    private Double applicationDose;
    private String applicationUnitTypeCode; // Nomenclature
    private String unitTypeName;
    private String applicationGrainCultureCode; // Classifier
    private String applicationGrainCultureName;
}
