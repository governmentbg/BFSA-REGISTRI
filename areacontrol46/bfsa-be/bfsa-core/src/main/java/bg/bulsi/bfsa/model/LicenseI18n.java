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
@Table(name = "licenses_i18n")
public class LicenseI18n extends AuditableEntity {

    @EmbeddedId
    private LicenseI18nIdentity licenseI18nIdentity;

    @Column(name = "name", length = 5000)
    private String name;

    @Column(name = "description", length = 5000)
    private String description;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private License license;

    public LicenseI18n(final String name,
                       final String description,
                       final License license,
                       final Language language) {
        this.licenseI18nIdentity = new LicenseI18nIdentity(license.getId(), language.getLanguageId());
        this.name = name;
        this.description = description;
    }

    public LicenseI18n(final License license, final Language language){
        this.licenseI18nIdentity = new LicenseI18nIdentity(license.getId(), language.getLanguageId());
        this.license = license;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LicenseI18nIdentity implements Serializable {

        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;
    }
}

