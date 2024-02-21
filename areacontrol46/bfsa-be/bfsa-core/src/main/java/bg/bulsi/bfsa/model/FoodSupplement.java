package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.FoodSupplementStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_supplements")
public class FoodSupplement extends BaseEntity {

    @Column(name = "reg_number", unique = true)
    private String regNumber;

    @Column(name = "reg_date")
    private LocalDate regDate;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "distribution_facility_address")
    private String distributionFacilityAddress;

    @Column(name = "manufacture_facility_address")
    private String manufactureFacilityAddress;

    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private Contractor applicant;

    // Nomenclature-library 02100: Food/food supplement type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_supplement_type_code")
    private Nomenclature foodSupplementType;

    // Nomenclature-library 01900: Measuring Units
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "measuring_unit_code")
    private Nomenclature measuringUnit;

    // Nomenclature-library 02000: Facility type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_type_code", nullable = false)
    private Nomenclature facilityType;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Country> countries = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FoodSupplementStatus status;

    @ManyToOne
    @JoinColumn(name = "application_s3125_id")
    private ApplicationS3125 applicationS3125;

    @Builder.Default
    @OneToMany(mappedBy = "foodSupplement", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private Set<FoodSupplementI18n> i18ns = new HashSet<>();

    public FoodSupplementI18n getI18n(final Language language) {
        if (language != null && StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
            for (FoodSupplementI18n i18n : i18ns) {
                if (language.getLanguageId().equals(i18n.getFoodSupplementI18nIdentity().getLanguageId())) {
                    return i18n;
                }
            }
        }
        FoodSupplementI18n i18n = new FoodSupplementI18n(this, language);
        i18ns.add(i18n);
        return i18n;
    }
}
