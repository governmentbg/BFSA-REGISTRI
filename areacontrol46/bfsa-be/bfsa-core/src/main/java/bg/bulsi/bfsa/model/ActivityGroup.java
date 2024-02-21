package bg.bulsi.bfsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.NotAudited;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "activity_groups")
public class ActivityGroup extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ActivityGroup parent;

    @Column(name = "enabled")
    private Boolean enabled;

    @Builder.Default
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private final Set<ActivityGroup> subActivityGroups = new HashSet<>();

    /*
        Кодове CS, RW
    */
    @NotAudited
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "activity_group_related_activity_categories",
            joinColumns = @JoinColumn(name="activity_group_id"),
            inverseJoinColumns = @JoinColumn(name="related_activity_category_code")
    )
    private Set<Nomenclature> relatedActivityCategories = new HashSet<>();

    /*
        Кодове CS, RW
    */
    @NotAudited
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name="activity_group_associated_activity_categories",
            joinColumns = @JoinColumn(name="activity_group_id"),
            inverseJoinColumns = @JoinColumn(name="associated_activity_category_code")
    )
    private Set<Nomenclature> associatedActivityCategories = new HashSet<>();

    /*
        Кодове A, B, C, L, ...
    */
    @NotAudited
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "activity_group_animal_species",
            joinColumns = @JoinColumn(name="activity_group_id"),
            inverseJoinColumns = @JoinColumn(name="animal_species_code")
    )
    private Set<Nomenclature> animalSpecies = new HashSet<>();

    /*
        Кодове A, B, C, L, ...
    */
    @NotAudited
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "activity_group_remarks",
            joinColumns = @JoinColumn(name="activity_group_id"),
            inverseJoinColumns = @JoinColumn(name="remark_code")
    )
    private Set<Nomenclature> remarks = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "activityGroup", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<ActivityGroupI18n> i18ns = new HashSet<>();

    public ActivityGroupI18n getI18n(final Language language) {
        if (StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (ActivityGroupI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getActivityGroupI18NIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        ActivityGroupI18n i18n = new ActivityGroupI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}