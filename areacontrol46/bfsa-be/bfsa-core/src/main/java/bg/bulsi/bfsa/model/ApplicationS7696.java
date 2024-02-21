package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.FacilityDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * ЗАЯВЛЕНИЕ за регистрация на обекти за производство и търговия с храниТърговия чрез подвижни/временни обекти
 */
@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s7696")
public class ApplicationS7696 extends ApplicationS769 {

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "facilities", columnDefinition = "json")
    private List<FacilityDTO> applicationS7696Facilities = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    private List<Facility> facilities = new ArrayList<>();


    @Builder.Default
    @OneToMany(mappedBy = "applicationS7696", cascade = CascadeType.ALL)
    private List<ApplicationS7696Vehicle> applicationS7696Vehicles = new ArrayList<>();

    public void addApplicationS7696Vehicle(Vehicle vehicle, Nomenclature ownershipType) {
        ApplicationS7696Vehicle applicationS7696Vehicle = new ApplicationS7696Vehicle(this, vehicle, ownershipType);
        applicationS7696Vehicles.add(applicationS7696Vehicle);
    }

    public void removeApplicationS7696Vehicle(Vehicle vehicle) {
        applicationS7696Vehicles.removeIf(av -> av.getVehicle().equals(vehicle));
    }
}
