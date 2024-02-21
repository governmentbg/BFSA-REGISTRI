package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.VehicleDTO;
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

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications_s2272")
public class ApplicationS2272 extends BaseApplication {

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_55_vehicles")
    private List<VehicleDTO> ch55vehicles = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "applicationS2272", cascade = CascadeType.ALL)
    private List<ApplicationS2272Vehicle> applicationS2272Vehicles = new ArrayList<>();

    public void addApplicationS2272Vehicle(Vehicle vehicle, Nomenclature ownerType) {
        ApplicationS2272Vehicle applicationS2272Vehicle = new ApplicationS2272Vehicle(this, vehicle, ownerType);
        applicationS2272Vehicles.add(applicationS2272Vehicle);
    }

    public void removeApplicationS2272Vehicle(Vehicle vehicle) {
        applicationS2272Vehicles.removeIf(cf -> cf.getVehicle().equals(vehicle));
    }
}
