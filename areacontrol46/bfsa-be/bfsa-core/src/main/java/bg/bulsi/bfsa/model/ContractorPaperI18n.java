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
@Table(name = "contractor_papers_i18n")
public class ContractorPaperI18n extends AuditableEntity {

    @EmbeddedId
    private ContractorPaperI18n.ContractorPaperI18nIdentity contractorPaperI18nIdentity;

    @Column(name = "name", length = 5000)
    private String name;
    @Column(name = "description", length = 5000)
    private String description;
    @Column(name = "educational_institution")
    private String educationalInstitution;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private ContractorPaper contractorPaper;

    public ContractorPaperI18n(final String name,
                               final String description,
                               final String educationalInstitution,
                               final ContractorPaper contractorPaper,
                               final Language language) {
        this.contractorPaperI18nIdentity =
                new ContractorPaperI18nIdentity(contractorPaper.getId(), language.getLanguageId());
        this.name = name;
        this.description = description;
        this.educationalInstitution = educationalInstitution;
        this.contractorPaper = contractorPaper;
    }

    public ContractorPaperI18n(final String name,
                               final String description,
                               final ContractorPaper contractorPaper,
                               final Language language) {
        this.name = name;
        this.description = description;
        this.contractorPaperI18nIdentity =
                new ContractorPaperI18n.ContractorPaperI18nIdentity(contractorPaper.getId(), language.getLanguageId());
        this.contractorPaper = contractorPaper;
    }

    public ContractorPaperI18n(final ContractorPaper contractorPaper, final Language language) {
        this.contractorPaperI18nIdentity =
                new ContractorPaperI18n.ContractorPaperI18nIdentity(contractorPaper.getId(), language.getLanguageId());
        this.contractorPaper = contractorPaper;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class ContractorPaperI18nIdentity implements Serializable {
        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }
}