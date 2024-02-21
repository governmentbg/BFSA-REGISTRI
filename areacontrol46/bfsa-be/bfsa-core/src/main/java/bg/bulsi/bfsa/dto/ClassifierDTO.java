package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.ClassifierI18n;
import bg.bulsi.bfsa.model.Language;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
// NOT_EMPTY breaks the frontend when render food classifier
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ClassifierDTO {

    private String code;
    private String parentCode;
    private String name;
    private Boolean enabled;
    private String description;
    private String symbol;
    private String externalCode;

    @Builder.Default
    private List<ClassifierDTO> subClassifiers = new ArrayList<>();

    public ClassifierDTO(final String code, final String name, final String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public static Classifier to(final ClassifierDTO source, final Language language) {
        Classifier entity = new Classifier();
        BeanUtils.copyProperties(source, entity);

        entity.getI18ns().add(new ClassifierI18n(source.getName(), source.getDescription(), entity, language));

        if (!CollectionUtils.isEmpty(source.getSubClassifiers())) {
            entity.getSubClassifiers().addAll(to(source.getSubClassifiers(), language));
        }

        return entity;
    }

    public static List<Classifier> to(final List<ClassifierDTO> sources, final Language language) {
        return sources.stream().map(s -> to(s, language)).collect(Collectors.toList());
    }

    public static ClassifierDTO of(final Classifier source, final Language language) {
        ClassifierDTO dto = new ClassifierDTO();
        BeanUtils.copyProperties(source, dto);

        dto.setParentCode(source.getParent() != null ? source.getParent().getCode() : null);

        ClassifierI18n i18n = source.getI18n(language);
        if(i18n != null) {
            dto.setName(i18n.getName());
            dto.setDescription(i18n.getDescription());
        }

        if (!CollectionUtils.isEmpty(source.getSubClassifiers())) {
            dto.getSubClassifiers().addAll(of(source.getSubClassifiers(), language));
        }

        return dto;
    }

    public static List<ClassifierDTO> of(final List<Classifier> sources, final Language language) {
        return sources.stream().map(s -> of(s, language)).collect(Collectors.toList());
    }
}
