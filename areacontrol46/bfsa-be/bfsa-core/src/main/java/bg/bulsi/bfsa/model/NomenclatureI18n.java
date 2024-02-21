package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
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
@Audited
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nomenclatures_i18n")
public class NomenclatureI18n extends AuditableEntity {

    @Serial
    private static final long serialVersionUID = 4823436757559820637L;

    @EmbeddedId
    private NomenclatureI18nIdentity nomenclatureI18nIdentity;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("code")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "code")
    private Nomenclature nomenclature;

    public NomenclatureI18n(final String name, final String description, final Nomenclature nomenclature, final Language language) {
        this.nomenclatureI18nIdentity = new NomenclatureI18nIdentity(nomenclature.getCode(), language.getLanguageId());
        this.name = StringUtils.hasText(name) ? name : "";
        this.description = description;
        this.nomenclature = nomenclature;
    }

    public NomenclatureI18n(final Nomenclature nomenclature, final Language language) {
        this.nomenclatureI18nIdentity = new NomenclatureI18nIdentity(nomenclature.getCode(), language.getLanguageId());
        this.nomenclature = nomenclature;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NomenclatureI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = 7659642703687711785L;

//        @NotNull
//        @Basic(optional = false)
        @Column(name = "code")
        private String code;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;

//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (!(obj instanceof NomenclatureI18nIdentity)) {
//            return false;
//        }
//        NomenclatureI18nIdentity that = (NomenclatureI18nIdentity) obj;
//        EqualsBuilder eb = new EqualsBuilder();
//        eb.append(this.code, that.code);
//        eb.append(this.languageId, that.languageId);
//        return eb.isEquals();
//    }
//
//    @Override
//    public int hashCode() {
//        HashCodeBuilder hcb = new HashCodeBuilder();
//        hcb.append(code);
//        hcb.append(languageId);
//        return hcb.toHashCode();
//    }
    }
}
