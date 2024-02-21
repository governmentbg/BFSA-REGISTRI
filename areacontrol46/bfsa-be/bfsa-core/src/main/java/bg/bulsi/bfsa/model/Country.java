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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "countries")
public class Country extends AuditableEntity {

    @Id
    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "isoAlpha3", length = 10)
    private String isoAlpha3;

    @Column(name = "continent", length = 10)
    private String continent;

    @Column(name = "currencyCode", length = 10)
    private String currencyCode;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "european_union_member")
    private Boolean europeanUnionMember;

    @Builder.Default
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
    private List<Settlement> settlements = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<CountryI18n> i18ns = new HashSet<>();

    public CountryI18n getI18n(final Language language) {
        if (StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (CountryI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getCountryI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }

        CountryI18n i18n = new CountryI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }

}
