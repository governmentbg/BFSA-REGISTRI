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

import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inspections_i18n")
public class InspectionI18n extends AuditableEntity {

    @EmbeddedId
    private InspectionI18nIdentity inspectionI18nIdentity;

    @Column(name = "name")
    private String name;
    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private Inspection inspection;

    public InspectionI18n(final String name, final String description, final Inspection inspection, final Language language) {
        this.inspectionI18nIdentity = new InspectionI18nIdentity(inspection.getId(), language.getLanguageId());
        this.name = name;
        this.description = description;
        this.inspection = inspection;
    }

    public InspectionI18n(final Inspection inspection, final Language language) {
        this.inspectionI18nIdentity = new InspectionI18nIdentity(inspection.getId(), language.getLanguageId());
        this.inspection = inspection;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InspectionI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = 6827130210171021926L;

        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }
}
