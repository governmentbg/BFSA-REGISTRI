package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.ServiceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contractor_papers")
public class ContractorPaper extends BaseApprovalDocument {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @Builder.Default
    @OneToMany(mappedBy = "contractorPaper", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ContractorPaperI18n> i18ns = new HashSet<>();

    public ContractorPaperI18n getI18n(final Language language) {
        if (StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (ContractorPaperI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getContractorPaperI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        ContractorPaperI18n i18n = new ContractorPaperI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}
