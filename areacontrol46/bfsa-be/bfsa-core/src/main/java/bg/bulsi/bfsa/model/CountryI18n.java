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
@Table(name = "countries_i18n")
public class CountryI18n extends AuditableEntity {

    @Serial
    private static final long serialVersionUID = 4823436757559820637L;

    @EmbeddedId
    private CountryI18nIdentity countryI18nIdentity;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "capital")
    private String capital;

    @Column(name = "continent_name")
    private String continentName;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("code")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "code")
    private Country country;

    public CountryI18n(final String name, final String capital, final String continentName, final String description,
                       final Country country, final Language language) {
        this.countryI18nIdentity = new CountryI18nIdentity(country.getCode(), language.getLanguageId());
        this.name = name;
        this.capital = capital;
        this.continentName = continentName;
        this.description = description;
        this.country = country;
    }

    public CountryI18n(final Country country, final Language language) {
        this.countryI18nIdentity = new CountryI18nIdentity(country.getCode(), language.getLanguageId());
        this.country = country;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryI18nIdentity implements Serializable {

        @Serial
        private static final long serialVersionUID = 6967380215723893900L;

//        @NotNull
//        @Basic(optional = false)
        @Column(name = "code")
        private String code;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }

}
