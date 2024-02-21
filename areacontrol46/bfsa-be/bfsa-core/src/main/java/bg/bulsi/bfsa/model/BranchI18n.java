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
import java.io.Serial;
import java.io.Serializable;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "branches_i18n")
public class BranchI18n extends AuditableEntity {

    @EmbeddedId
    private BranchI18nIdentity branchI18nIdentity;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private Branch branch;

    public BranchI18n(final String name, final String address, final String description, final Branch branch, final Language language) {
        this.branchI18nIdentity = new BranchI18nIdentity(branch.getId(), language.getLanguageId());
        this.name = name;
        this.address = address;
        this.description = description;
        this.branch = branch;
    }

    public BranchI18n(final Branch branch, final Language language) {
        this.branchI18nIdentity = new BranchI18nIdentity(branch.getId(), language.getLanguageId());
        this.branch = branch;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = 1931534676900469935L;

        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }
}
