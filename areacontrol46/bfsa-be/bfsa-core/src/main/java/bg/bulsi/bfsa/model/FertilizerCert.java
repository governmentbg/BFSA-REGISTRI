package bg.bulsi.bfsa.model;

import jakarta.persistence.ManyToOne;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/*
Електронен регистър - 10. Регистър на торове, подобрители на почвата,
биологично активни вещества и хранителни субстрати,
за които е издадено удостоверение за пускане на пазара и употреба
 */

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fertilizer_cert")
public class FertilizerCert extends BaseEntity {

    @Column(name = "reg_number")
    private String regNumber;
    @Column(name = "reg_date")
    private LocalDate regDate;
    @Column(name = "entry_date")
    private LocalDate entryDate;
    @Column(name = "valid_until_date")
    private LocalDate validUntilDate;
    @Column(name = "edition")
    private String edition;
    @Column(name = "ph")
    private Double ph;
    @Column(name = "dose")
    private Double dose;
    @Column(name = "water_amount")
    private Double waterAmount;
    @Column(name = "app_number")
    private Integer appNumber;
    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "order_date")
    private LocalDate orderDate;
    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fertilizer_type_code")
    private Nomenclature fertilizerType;
    // Вид на продукта; (fertilizerTypeCode, OneToOne връзка към номенклатури)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    private Contractor manufacturer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_holder_id")
    private Contractor certificateHolder;

    @Builder.Default
    @OneToMany(mappedBy = "fertilizerCert", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<FertilizerCertI18n> i18ns = new HashSet<>();

    public FertilizerCertI18n getI18n(final Language language) {
        if (StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (FertilizerCertI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getFertilizerCertI18NIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        FertilizerCertI18n i18n = new FertilizerCertI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}