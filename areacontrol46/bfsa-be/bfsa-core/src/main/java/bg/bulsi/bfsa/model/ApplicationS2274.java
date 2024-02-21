package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.ApplicationS2274ActiveSubstanceDTO;
import bg.bulsi.bfsa.dto.ApplicationS2274PackageDTO;
import bg.bulsi.bfsa.dto.ApplicationS2274ReferenceProductDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.NotAudited;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s2274")
public class ApplicationS2274 extends OrderApplication {
    // 1. Притежател на разрешението за пускане на пазара и употреба на ПРЗ:
    @Column(name = "permit_holder_identifier")
    private String permitHolderIdentifier;
    @Column(name = "permit_holder_name")
    private String permitHolderName;
    @Column(name = "permit_holder_name_lat")
    private String permitHolderNameLat;
    @Column(name = "permit_holder_headquarter")
    private String permitHolderHeadquarter;
    @Column(name = "permit_holder_headquarter_lat")
    private String permitHolderHeadquarterLat;
    @Column(name = "permit_holder_address")
    private String permitHolderAddress;
    @Column(name = "permit_holder_address_lat")
    private String permitHolderAddressLat;
    @Column(name = "permit_holder_email")
    private String permitHolderEmail;

    // 2. Идентификация на продукта
    @Column(name = "ppp_name")
    private String pppName;
    @Column(name = "ppp_code")
    private String pppCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formulation_type_code", referencedColumnName = "code")
    private Nomenclature formulationType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ppp_manufacturers")
    private Map<String, String> pppManufacturers = new HashMap<>();

    @NotAudited
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "applications_s2274_plant_protection_product_functions",
            joinColumns = @JoinColumn(name="applications_s2274_id"),
            inverseJoinColumns = @JoinColumn(name="plant_protection_product_function_code")
    )
    private Set<Nomenclature> plantProtectionProductFunctions = new HashSet<>();

    @NotAudited
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "applications_s2274_plant_protection_product_actions",
            joinColumns = @JoinColumn(name="applications_s2274_id"),
            inverseJoinColumns = @JoinColumn(name="plant_protection_product_action_code")
    )
    private Set<Nomenclature> plantProtectionProductActions = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_protection_product_category_code", referencedColumnName = "code")
    private Nomenclature plantProtectionProductCategory;

    // 2.7. Идентификация на активните вещества
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "active_substances")
    private List<ApplicationS2274ActiveSubstanceDTO> activeSubstances = new ArrayList<>();

    @Column(name = "ppp_description")
    private String pppDescription;

    // 4. Характеристики на опаковката
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "packages", columnDefinition = "json")
    private List<ApplicationS2274PackageDTO> packages = new ArrayList<>();

    // 5. Допълнителна информация
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reference_products")
    private List<ApplicationS2274ReferenceProductDTO> referenceProducts = new ArrayList<>();

}
