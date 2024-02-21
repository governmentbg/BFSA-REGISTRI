package bg.bulsi.bfsa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "branches")
public class Branch extends BaseEntity {

    @Column(name = "sequence_number")
    private String sequenceNumber;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone1")
    private String phone1;

    @Column(name = "phone2")
    private String phone2;

    @Column(name = "phone3")
    private String phone3;

    @Column(name = "main")
    private Boolean main = false;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "identifier", unique = true)
    private String identifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_code", nullable = false)
    private Settlement settlement;

    @Builder.Default
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<BranchI18n> i18ns = new HashSet<>();

    public BranchI18n getI18n(final Language language) {
        if (language != null && StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (BranchI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getBranchI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        BranchI18n i18n = new BranchI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}
