package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.KeyValueDTO;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.NotAudited;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s1366_products")
public class ApplicationS1366Product extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "application_s1366_id")
    private ApplicationS1366 applicationS1366;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "food_types", columnDefinition = "json")
    private Set<KeyValueDTO> applicationS1366ProductFoodTypes = new HashSet<>();

    @NotAudited
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "applications_s1366_product_food_types",
            joinColumns = @JoinColumn(name = "applications_s1366_product_id"),
            inverseJoinColumns = @JoinColumn(name = "food_type_code")
    )
    private Set<Classifier> foodTypes = new HashSet<>();

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_trademark")
    private String productTrademark;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_country_code")
    private Country productCountry;
    // Veterinary
    @Column(name = "product_total_net_weight")
    private Double productTotalNetWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_total_net_weight_unit_code")
    private Nomenclature productTotalNetWeightUnit;
    // Health
    @Column(name = "product_package_type")
    private String productPackageType;

    @Column(name = "product_expiry_date")
    private OffsetDateTime productExpiryDate;

    @Column(name = "product_manufacture_date")
    private OffsetDateTime productManufactureDate;

    @Builder.Default
    @OneToMany(mappedBy = "applicationS1366Product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ApplicationS1366ProductBatch> applicationS1366ProductBatches = new ArrayList<>();

}
