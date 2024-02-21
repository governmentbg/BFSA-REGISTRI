package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.InspectionStatus;
import bg.bulsi.bfsa.enums.InspectionType;
import bg.bulsi.bfsa.enums.RiskLevel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inspections")
public class Inspection extends BaseEntity {

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private InspectionType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InspectionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "record_id")
    private Record record;

    @Builder.Default
    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> attachments = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(name = "inspections_users",
            joinColumns = @JoinColumn(name = "inspection_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users = new ArrayList<>();

    // Nomenclature-library 02500
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "inspections_reasons",
            joinColumns = @JoinColumn(name = "inspection_id"),
            inverseJoinColumns = @JoinColumn(name = "reason_code"))
    private List<Nomenclature> reasons = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "inspection", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<InspectionI18n> i18ns = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    public InspectionI18n getI18n(final Language language) {
        if (language != null && StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (InspectionI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getInspectionI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        InspectionI18n i18n = new InspectionI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}
