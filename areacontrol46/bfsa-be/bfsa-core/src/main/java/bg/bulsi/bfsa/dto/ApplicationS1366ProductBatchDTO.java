package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.ApplicationS1366ProductBatch;
import bg.bulsi.bfsa.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationS1366ProductBatchDTO {
    private String batchNumber;
    private Double perUnitNetWeight;
    private String perUnitNetWeightUnitCode;
    private String perUnitNetWeightUnitName;
    private Double batchNetWeight;
    private String batchNetWeightUnitCode;
    private String batchNetWeightUnitName;
    private Integer unitsInBatch;

    public static ApplicationS1366ProductBatch to(ApplicationS1366ProductBatchDTO source) {
        ApplicationS1366ProductBatch entity = new ApplicationS1366ProductBatch();
        BeanUtils.copyProperties(source, entity);

        return entity;
    }

    public static List<ApplicationS1366ProductBatch> to(final List<ApplicationS1366ProductBatchDTO> source) {
        return source.stream().map(ApplicationS1366ProductBatchDTO::to).collect(Collectors.toList());
    }

    public static ApplicationS1366ProductBatchDTO of(final ApplicationS1366ProductBatch source, Language language) {
        ApplicationS1366ProductBatchDTO dto = new ApplicationS1366ProductBatchDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getPerUnitNetWeightUnit() != null) {
            dto.setPerUnitNetWeightUnitCode(source.getPerUnitNetWeightUnit().getCode());
            dto.setPerUnitNetWeightUnitName(source.getPerUnitNetWeightUnit().getI18n(language).getName());
        }

        if (source.getBatchNetWeightUnit() != null) {
            dto.setBatchNetWeightUnitCode(source.getBatchNetWeightUnit().getCode());
            dto.setBatchNetWeightUnitName(source.getBatchNetWeightUnit().getI18n(language).getName());
        }

        return dto;
    }

    public static List<ApplicationS1366ProductBatchDTO> of(final List<ApplicationS1366ProductBatch> source, final Language language) {
        return source.stream().map(r -> of(r, language)).collect(Collectors.toList());
    }

}
