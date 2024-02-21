package bg.bulsi.bfsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Audited
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nomenclatures")
public class Nomenclature extends AuditableEntity {

    @Id
    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "enabled")
    private Boolean enabled;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    private Nomenclature parent;

    @Column(name = "symbol", length = 20000)
    private String symbol;

    @Column(name = "external_code")
    private String externalCode;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private final List<Nomenclature> subNomenclatures = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "nomenclature", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<NomenclatureI18n> i18ns = new HashSet<>();

    public NomenclatureI18n getI18n(final Language language) {
        if (StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (NomenclatureI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getNomenclatureI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        NomenclatureI18n i18n = new NomenclatureI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }

}
