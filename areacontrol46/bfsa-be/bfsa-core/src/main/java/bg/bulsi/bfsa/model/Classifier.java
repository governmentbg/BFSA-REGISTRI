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
@Table(name = "classifiers")
public class Classifier extends AuditableEntity {

    @Id
    @Column(name = "code", length = 30)
    private String code;

    @Column(name = "external_code", length = 100)
    private String externalCode;

    @Column(name = "enabled")
    private Boolean enabled;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code")
    private Classifier parent;

    @Column(name = "symbol", length = 20000)
    private String symbol;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private final List<Classifier> subClassifiers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "classifier", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ClassifierI18n> i18ns = new HashSet<>();

    public ClassifierI18n getI18n(final Language language) {
        if (StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (ClassifierI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getClassifierI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        ClassifierI18n i18n = new ClassifierI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}
