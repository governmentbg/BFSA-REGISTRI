package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.FacilityStatus;
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
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
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
@Table(name = "facilities")
public class Facility extends BaseEntity {

    @Column(name = "reg_number")
    private String regNumber;

    @Column(name = "reg_date")
    private LocalDate regDate;

    @Column(name = "mail")
    private String mail;

    @Column(name = "phone1")
    private String phone1;

    @Column(name = "phone2")
    private String phone2;

    // Капацитет на обекта: (число)
    @Column(name = "capacity")
    private Double capacity;

    // Площ на обекта: (число)
    @Column(name = "area")
    private Double area;

    @Column(name = "disposal_waste_water")
    private String disposalWasteWater;

    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FacilityStatus status;

    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_group_id")
    private ActivityGroup activityGroup;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "facility_related_activity_categories",
            joinColumns = @JoinColumn(name = "facility_id"),
            inverseJoinColumns = @JoinColumn(name = "related_activity_category_code")
    )
    private Set<Nomenclature> relatedActivityCategories = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "facility_associated_activity_categories",
            joinColumns = @JoinColumn(name = "facility_id"),
            inverseJoinColumns = @JoinColumn(name = "associated_activity_category_code")
    )
    private Set<Nomenclature> associatedActivityCategories = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "facility_animal_species",
            joinColumns = @JoinColumn(name = "facility_id"),
            inverseJoinColumns = @JoinColumn(name = "animal_species_code")
    )
    private Set<Nomenclature> animalSpecies = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "facility_remarks",
            joinColumns = @JoinColumn(name = "facility_id"),
            inverseJoinColumns = @JoinColumn(name = "remark_code")
    )
    private Set<Nomenclature> remarks = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "facility_pictograms",
            joinColumns = @JoinColumn(name = "facility_id"),
            inverseJoinColumns = @JoinColumn(name = "pictogram_code")
    )
    private Set<Nomenclature> pictograms = new HashSet<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "facility_food_types",
            joinColumns = @JoinColumn(name = "facility_id"),
            inverseJoinColumns = @JoinColumn(name = "food_type_code")
    )
    private Set<Classifier> foodTypes = new HashSet<>();

    @NotAudited
    @Builder.Default
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityPaper> facilityPapers = new ArrayList<>();

    @NotAudited
    @Builder.Default
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL)
    private List<Inspection> inspections = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    @JoinTable(name = "facilities_registers", joinColumns = @JoinColumn(name = "facility_id"),
            inverseJoinColumns = @JoinColumn(name = "code"))
    private List<Classifier> registers = new ArrayList<>();

    // Nomenclature-library 01800: Activity Type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_type_code")
    private Nomenclature activityType;

    // Nomenclature-library 02000: Facility Type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_type_code")
    private Nomenclature facilityType;

    // Nomenclature-library 02400: Type of water supply in the facility
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "water_supply_type_code")
    private Nomenclature waterSupplyType;

    // Капацитет на обекта
    @NotAudited
    @Builder.Default
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityCapacity> facilityCapacities = new ArrayList<>();

    //Мерна единица (избор от номенклатура)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measuring_unit_code")
    private Nomenclature measuringUnit;

    //Период (избор от номенклатура)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_code")
    private Nomenclature period;

    //Вид склад, само при избор на "Търговия" (избор от номенклатура - Nomenclature-library 01100)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_warehouse_code")
    private Nomenclature subActivityType;

    @ManyToMany
    @Builder.Default
    @JoinTable(name = "facilities_vehicles", joinColumns = @JoinColumn(name = "contractor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private List<Vehicle> vehicles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<FacilityI18n> i18ns = new HashSet<>();

    public FacilityI18n getI18n(final Language language) {
        if (language != null && StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (FacilityI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getFacilityI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        FacilityI18n i18n = new FacilityI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}
