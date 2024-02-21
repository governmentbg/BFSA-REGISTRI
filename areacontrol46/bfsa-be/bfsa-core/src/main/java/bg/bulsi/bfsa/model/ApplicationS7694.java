package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.KeyValueDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "applications_s7694")
public class ApplicationS7694 extends ApplicationS769 {

    @Column(name = "name")
    private String name;

    @Column(name = "food_type_description")
    private String foodTypeDescription;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "food_types", columnDefinition = "json")
    private Set<KeyValueDTO> applicationS7694FoodTypes = new HashSet<>();

    @NotAudited
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "applications_s7694_food_types",
            joinColumns = @JoinColumn(name = "applications_s7694_id"),
            inverseJoinColumns = @JoinColumn(name = "food_type_code")
    )
    private Set<Classifier> foodTypes = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "applicationS7694", cascade = CascadeType.ALL)
    private List<ApplicationS7694Vehicle> applicationS7694Vehicles = new ArrayList<>();

    public void addApplicationS7694Vehicle(Vehicle vehicle, Nomenclature ownershipType) {
        ApplicationS7694Vehicle applicationS7694Vehicle = new ApplicationS7694Vehicle(this, vehicle, ownershipType);
        applicationS7694Vehicles.add(applicationS7694Vehicle);
    }

    public void removeApplicationS7694Vehicle(Vehicle vehicle) {
        applicationS7694Vehicles.removeIf(av -> av.getVehicle().equals(vehicle));
    }
}
