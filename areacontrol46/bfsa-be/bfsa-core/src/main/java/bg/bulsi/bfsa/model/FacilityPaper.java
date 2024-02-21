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
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facility_papers")
public class FacilityPaper extends BaseApprovalDocument {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @Builder.Default
    @OneToMany(mappedBy = "facilityPaper", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<FacilityPaperI18n> i18ns = new HashSet<>();

    public FacilityPaperI18n getI18n(final Language language) {
        if (StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (FacilityPaperI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getFacilityPaperI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }

        FacilityPaperI18n i18n = new FacilityPaperI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}
