package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Language;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class LanguageDTO {

    private String languageId;
    private String name;
    private String locale;
    private String description;
    private Boolean main;
    private Boolean enabled;

    public static Language to(final LanguageDTO dto) {
        Language entity = new Language();
        BeanUtils.copyProperties(dto, entity);

        return entity;
    }

    public static List<Language> to(final List<LanguageDTO> dtos) {
        return dtos.stream().map(LanguageDTO::to).collect(Collectors.toList());
    }

    public static LanguageDTO of(final Language entity) {
        LanguageDTO dto = new LanguageDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public static List<LanguageDTO> of(final List<Language> entities) {
        return entities.stream().map(LanguageDTO::of).collect(Collectors.toList());
    }
}
