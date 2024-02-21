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
@Table(name = "applications_s7695")
public class ApplicationS7695 extends ApplicationS769 {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @Column(name = "food_type_description")
    private String foodTypeDescription;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address; // distanceTrading

    @Column(name = "facility_activity_description")
    private String facilityActivityDescription;

    @Column(name = "capacity_usage")
    private Double capacityUsage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_type_code")
    private Nomenclature unitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_type_code")
    private Nomenclature periodType;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "food_types", columnDefinition = "json")
    private Set<KeyValueDTO> applicationS7695FoodTypes = new HashSet<>();

    @NotAudited
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "applications_s7695_food_types",
            joinColumns = @JoinColumn(name = "applications_s7695_id"),
            inverseJoinColumns = @JoinColumn(name = "food_type_code")
    )
    private Set<Classifier> foodTypes = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "applicationS7695", cascade = CascadeType.ALL)
    private List<ApplicationS7695Vehicle> applicationS7695Vehicles = new ArrayList<>();

    public void addApplicationS7695Vehicle(Vehicle vehicle, Nomenclature ownershipType) {
        ApplicationS7695Vehicle applicationS7695Vehicle = new ApplicationS7695Vehicle(this, vehicle, ownershipType);
        applicationS7695Vehicles.add(applicationS7695Vehicle);
    }

    public void removeApplicationS7695Vehicle(Vehicle vehicle) {
        applicationS7695Vehicles.removeIf(av -> av.getVehicle().equals(vehicle));
    }
}
