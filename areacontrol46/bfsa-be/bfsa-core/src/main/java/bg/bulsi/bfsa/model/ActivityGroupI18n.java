package bg.bulsi.bfsa.model;

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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "activity_groups_i18n")
public class ActivityGroupI18n extends AuditableEntity {

    @Serial
    private static final long serialVersionUID = 4823436757559820637L;

    @EmbeddedId
    private ActivityGroupI18nIdentity activityGroupI18NIdentity;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private ActivityGroup activityGroup;

    public ActivityGroupI18n(final String name, final String description, final ActivityGroup activityGroup, final Language language) {
        this.activityGroupI18NIdentity = new ActivityGroupI18nIdentity(activityGroup.getId(), language.getLanguageId());
        this.name = StringUtils.hasText(name) ? name : "";
        this.description = description;
        this.activityGroup = activityGroup;
    }

    public ActivityGroupI18n(final ActivityGroup activityGroup, final Language language) {
        this.activityGroupI18NIdentity = new ActivityGroupI18nIdentity(activityGroup.getId(), language.getLanguageId());
        this.activityGroup = activityGroup;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityGroupI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = 2746414774131222405L;

        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;

    }
}