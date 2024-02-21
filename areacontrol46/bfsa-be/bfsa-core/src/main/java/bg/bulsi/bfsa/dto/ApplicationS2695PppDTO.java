package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.ApplicationS2695Ppp;
import bg.bulsi.bfsa.model.Language;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2695PppDTO {
    private String pppFunctionCode;
    private String pppFunctionName;
    private String pppName;
    private Boolean pppAerialSpray;
    private String pppPurchase;
    private Double pppDose;
    private String pppUnitCode;
    private String pppUnitName;
    private Integer pppQuarantinePeriod;
    private List<PppPestBO> pppPests = new ArrayList<>();

    public static ApplicationS2695Ppp to(final ApplicationS2695PppDTO source) {
        ApplicationS2695Ppp entity = new ApplicationS2695Ppp();
        BeanUtils.copyProperties(source, entity);

//        if (StringUtils.hasText(source.getPppFunctionCode())) {
//            entity.setPppFunction(Nomenclature.builder().code(source.getPppFunctionCode()).build());
//        }
//        if (StringUtils.hasText(source.getPppUnitCode())) {
//            entity.setPppUnit(Nomenclature.builder().code(source.getPppUnitCode()).build());
//        }
        if (!CollectionUtils.isEmpty(source.getPppPests())) {
            entity.setPppPests(source.pppPests);
        }
        return entity;
    }

    public static List<ApplicationS2695Ppp> to(final List<ApplicationS2695PppDTO> source) {
        return source.stream().map(ApplicationS2695PppDTO::to).collect(Collectors.toList());
    }

    public static ApplicationS2695PppDTO of(final ApplicationS2695Ppp source, final Language language) {
        ApplicationS2695PppDTO dto = new ApplicationS2695PppDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getPppFunction() != null) {
            dto.setPppFunctionCode(source.getPppFunction().getCode());
            dto.setPppFunctionName(source.getPppFunction().getI18n(language).getName());
        }
        if (source.getPppUnit() != null) {
            dto.setPppUnitCode(source.getPppUnit().getCode());
            dto.setPppUnitName(source.getPppUnit().getI18n(language).getName());
        }
        return dto;
    }

    public static List<ApplicationS2695PppDTO> of(final List<ApplicationS2695Ppp> source, Language language) {
        return source.stream().map(s -> of(s, language)).collect(Collectors.toList());
    }
}
