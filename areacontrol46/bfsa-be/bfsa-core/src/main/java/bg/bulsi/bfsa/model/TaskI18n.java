package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks_i18n")
public class TaskI18n extends AuditableEntity {

    @EmbeddedId
    private TaskI18nIdentity taskI18nIdentity;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private Task task;

    public TaskI18n(final String name, final String description, final Task task, final Language language) {
        this.taskI18nIdentity = new TaskI18nIdentity(task.getId(), language.getLanguageId());
        this.name = StringUtils.hasText(name) ? name : "";
        this.description = description;
        this.task = task;
    }

    public TaskI18n(final Task task, final Language language) {
        this.taskI18nIdentity = new TaskI18nIdentity(task.getId(), language.getLanguageId());
        this.task = task;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = -1101988586387554054L;

//        @NotNull
//        @Basic(optional = false)
        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;

//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (!(obj instanceof TaskI18nIdentity)) {
//            return false;
//        }
//        TaskI18nIdentity that = (TaskI18nIdentity) obj;
//        EqualsBuilder eb = new EqualsBuilder();
//        eb.append(this.id, that.id);
//        eb.append(this.languageId, that.languageId);
//        return eb.isEquals();
//    }
//
//    @Override
//    public int hashCode() {
//        HashCodeBuilder hcb = new HashCodeBuilder();
//        hcb.append(id);
//        hcb.append(languageId);
//        // System.out.println("HashCode: [" + hcb.toHashCode() + "]");
//        return hcb.toHashCode();
//    }
    }
}
