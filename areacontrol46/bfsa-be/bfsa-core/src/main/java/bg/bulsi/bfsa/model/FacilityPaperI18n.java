package bg.bulsi.bfsa.model;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
import java.io.Serializable;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facility_papers_i18n")
public class FacilityPaperI18n extends AuditableEntity {
    @EmbeddedId
    private FacilityPaperI18n.FacilityPaperI18nIdentity facilityPaperI18nIdentity;

    @Column(name = "name", length = 5000)
    private String name;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private FacilityPaper facilityPaper;

    public FacilityPaperI18n(final String name, final String description,
                             final FacilityPaper facilityPaper, final Language language) {
        this.facilityPaperI18nIdentity = new FacilityPaperI18nIdentity(facilityPaper.getId(), language.getLanguageId());
        this.name = name;
        this.description = description;
        this.facilityPaper = facilityPaper;
    }

    public FacilityPaperI18n(final FacilityPaper facilityPaper, final Language language) {
        this.facilityPaperI18nIdentity =
                new FacilityPaperI18n.FacilityPaperI18nIdentity(facilityPaper.getId(), language.getLanguageId());

        this.facilityPaper = facilityPaper;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class FacilityPaperI18nIdentity implements Serializable {
        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }
}
