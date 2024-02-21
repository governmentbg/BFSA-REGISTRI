package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.MessageResource;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class MessageResourceDTO {

    private String code;
    private String languageId;
    private String message;

    public static MessageResource to(final MessageResourceDTO source) {
        MessageResource entity = new MessageResource();
        BeanUtils.copyProperties(source, entity);

        return entity;
    }

    public static List<MessageResource> to(final List<MessageResourceDTO> sources) {
        return sources.stream().map(MessageResourceDTO::to).collect(Collectors.toList());
    }

    public static MessageResourceDTO of(final MessageResource source) {
        MessageResourceDTO dto = new MessageResourceDTO();
        BeanUtils.copyProperties(source, dto);

        dto.setCode(source.getMessageResourceIdentity().getCode());
        dto.setLanguageId(source.getMessageResourceIdentity().getLanguageId());

        return dto;
    }

    public static List<MessageResourceDTO> of(final List<MessageResource> entities) {
        return entities.stream().map(MessageResourceDTO::of).collect(Collectors.toList());
    }

    public static List<MessageResourceDTO> of(final List<MessageResource> entities, List<Language> languages) {
        if (!CollectionUtils.isEmpty(entities) && !CollectionUtils.isEmpty(languages) && languages.size() > entities.size()) {
            languages.forEach(l -> {
                MessageResource mr = entities.stream().filter(e -> l.getLanguageId().equals(e.getMessageResourceIdentity().getLanguageId())).findAny().orElse(null);
                if (mr == null) {
                    MessageResource.MessageResourceIdentity identity = new MessageResource.MessageResourceIdentity(entities.get(0).getMessageResourceIdentity().getCode(), l.getLanguageId());
                    identity.setLanguageId(l.getLanguageId());
                    entities.add(MessageResource.builder().messageResourceIdentity(identity).message("").build());
                }
            });
        }
        return entities.stream().map(MessageResourceDTO::of).collect(Collectors.toList());
    }
}
