package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="contractor_id")
    private Contractor contractor;

    @Builder.Default
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<TaskI18n> i18ns = new HashSet<>();

    public TaskI18n getI18n(final Language language) {
        if (StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (TaskI18n i18n : i18ns) {
                if (StringUtils.hasText(i18n.getTaskI18nIdentity().getLanguageId()) &&
                        language.getLanguageId().equals(i18n.getTaskI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        TaskI18n i18n = new TaskI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}
