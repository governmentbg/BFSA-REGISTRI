package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class KeyValueDTO {
    private Long id;
    private String code;
    private String key;

    private String name;
    private String description;
    private String value;
    private String status;
    private Double number;

    private KeyValueDTO parent;

    @Builder.Default
    private List<KeyValueDTO> subKeyValues = new ArrayList<>();

    public static KeyValueDTO of(final String code, final String value) {
        return KeyValueDTO.builder().code(code).value(value).build();
    }

    public static List<KeyValueDTO> ofNomenclatures(final List<Nomenclature> entities, final Language language) {
        return entities.stream().map(n -> of(n.getCode(), n.getI18n(language).getName())).collect(Collectors.toList());
    }

    public static KeyValueDTO ofClassifier(final Classifier classifier, final Language language) {
        return KeyValueDTO.builder()
                .parent(classifier.getParent() != null ? ofClassifier(classifier.getParent(), language) : null)
                .code(classifier.getCode())
                .name(classifier.getI18n(language).getName()).build();
    }

    public static Set<KeyValueDTO> ofClassifiers(final Set<Classifier> source, final Language language) {
        return source.stream().map(c -> ofClassifier(c, language)).collect(Collectors.toSet());
    }

    public void addSubKeyValue(KeyValueDTO keyValue) {
        this.subKeyValues.add(keyValue);
        keyValue.setParent(this);
    }
}