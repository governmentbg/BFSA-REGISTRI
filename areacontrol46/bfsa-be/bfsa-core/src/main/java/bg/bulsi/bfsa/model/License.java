package bg.bulsi.bfsa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "licenses")
public class License extends BaseApprovalDocument {
    @Column(name = "entry_permit_date")
    private LocalDate entryPermitDate;

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    @Column(name = "valid_date")
    private LocalDate validDate;

    @Column(name = "change_permit_date")
    private LocalDate changePermitDate;

    @Column(name = "change_permit_number")
    private String changePermitNumber;

    @Column(name = "decision_number")
    private String decisionNumber;

    @ManyToOne(targetEntity = Contractor.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id")
    private Contractor licenseHolder;

    @Builder.Default
    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<LicenseI18n> i18ns = new HashSet<>();

    public LicenseI18n getI18n(final Language language) {
        if (language != null && StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (LicenseI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getLicenseI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        LicenseI18n i18n = new LicenseI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }

}
