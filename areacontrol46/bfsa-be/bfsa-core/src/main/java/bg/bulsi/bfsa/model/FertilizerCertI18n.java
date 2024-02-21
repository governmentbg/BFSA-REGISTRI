package bg.bulsi.bfsa.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fertilizer_certs_i18n")
public class FertilizerCertI18n extends AuditableEntity {

    @Serial
    private static final long serialVersionUID = 4823436757559820637L;

    @EmbeddedId
    private FertilizerCertI18nIdentity fertilizerCertI18NIdentity;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "ingredients", length = 5000)
    private String ingredients; // състав
    @Column(name = "wording", length = 5000)
    private String wording; // Формулация
    @Column(name = "crop", length = 5000)
    private String crop; // Култура
    @Column(name = "application", length = 5000)
    private String application; // Приложение на продукта
    @Column(name = "reason", length = 5000)
    private String reason; // Основание ЗЗР;
    @Column(name = "description", length = 5000)
    private String description; // Забележки

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id")
    private FertilizerCert fertilizerCert;

    public FertilizerCertI18n(String name, String ingredients, String wording,
                              String crop, String application, String reason,
                              String description, FertilizerCert fertilizerCert,
                              Language language) {
        this.fertilizerCertI18NIdentity = new FertilizerCertI18nIdentity(fertilizerCert.getId(), language.getLanguageId());
        this.name = StringUtils.hasText(name) ? name : "";
        this.ingredients = StringUtils.hasText(ingredients) ? ingredients : "";
        this.wording = StringUtils.hasText(wording) ? wording : "";
        this.crop = StringUtils.hasText(crop) ? crop : "";
        this.application = StringUtils.hasText(application) ? application : "";
        this.reason = StringUtils.hasText(reason) ? reason : "";
        this.description = StringUtils.hasText(description) ? description : "";
        this.fertilizerCert = fertilizerCert;
    }

    public FertilizerCertI18n(final FertilizerCert certifiedFertilizer, final Language language) {
        this.fertilizerCertI18NIdentity = new FertilizerCertI18nIdentity(certifiedFertilizer.getId(), language.getLanguageId());
        this.fertilizerCert = certifiedFertilizer;
    }

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FertilizerCertI18nIdentity implements Serializable {
        @Serial
        private static final long serialVersionUID = -4024323482530387148L;

        @Column(name = "id")
        private Long id;

        @NotNull
        @Basic(optional = false)
        @Column(name = "language_id")
        private String languageId;

    }
}