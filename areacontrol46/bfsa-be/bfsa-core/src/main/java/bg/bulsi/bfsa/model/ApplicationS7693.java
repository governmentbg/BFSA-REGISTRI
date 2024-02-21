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
@Table(name = "applications_s7693")
public class ApplicationS7693 extends ApplicationS769 {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @Column(name = "food_type_description")
    private String foodTypeDescription;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address; // distanceTrading

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "food_types", columnDefinition = "json")
    private Set<KeyValueDTO> applicationS7693FoodTypes = new HashSet<>();

    @NotAudited
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "applications_s7693_food_types",
            joinColumns = @JoinColumn(name = "applications_s7693_id"),
            inverseJoinColumns = @JoinColumn(name = "food_type_code")
    )
    private Set<Classifier> foodTypes = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "applicationS7693", cascade = CascadeType.ALL)
    private List<ApplicationS7693Vehicle> applicationS7693Vehicles = new ArrayList<>();

    public void addApplicationS7693Vehicle(Vehicle vehicle, Nomenclature ownershipType) {
        ApplicationS7693Vehicle applicationS7693Vehicle = new ApplicationS7693Vehicle(this, vehicle, ownershipType);
        applicationS7693Vehicles.add(applicationS7693Vehicle);
    }

    public void removeApplicationS7693Vehicle(Vehicle vehicle) {
        applicationS7693Vehicles.removeIf(av -> av.getVehicle().equals(vehicle));
    }
}
