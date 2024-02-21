package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Task;
import bg.bulsi.bfsa.model.TaskI18n;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;
    private String name;
    private String description;
    private Long roleId;
    private Long userId;

    public static TaskDTO of(final Task source, final Language language) {
        TaskDTO dto = new TaskDTO();
        dto.setId(source.getId());
        dto.setRoleId(source.getRole().getId());

        if (source.getUser() != null) {
            dto.setUserId(source.getUser().getId());
        }

        TaskI18n i18n = source.getI18n(language);
        if (i18n != null) {
            dto.setName(i18n.getName());
            dto.setDescription(i18n.getDescription());
        }

        return dto;
    }

    public static List<TaskDTO> of(final List<Task> source, final Language language) {
        return source.stream().map(t -> of(t, language)).collect(Collectors.toList());
    }

    public static Task to(final TaskDTO source, final Language language) {
        Task entity = new Task();
        entity.getI18ns().add(new TaskI18n(source.getName(), source.getDescription(), entity, language));

        return entity;
    }
}
