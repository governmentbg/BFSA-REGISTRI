package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.VehicleDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications_s1811")
public class ApplicationS1811 extends BaseApplication {

    @Column(name = "facility_reg_number")
    private String facilityRegNumber;

    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_50_vehicle_paper_reg_numbers")
    private List<String> ch50vehiclePaperRegNumbers = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_50_vehicles")
    private List<VehicleDTO> ch50vehicles = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_52_vehicles")
    private List<VehicleDTO> ch52vehicles = new ArrayList<>();


//    @Builder.Default
//    @OneToMany(cascade = CascadeType.ALL)
//    private List<ApplicationS1811Vehicle> applicationS1811Vehicles = new ArrayList<>();
//
//    public void addApplicationS1811Vehicle(Vehicle vehicle, Nomenclature ownerType) {
//        ApplicationS1811Vehicle applicationS1811Vehicle = new ApplicationS1811Vehicle(this, vehicle, ownerType);
//        applicationS1811Vehicles.add(applicationS1811Vehicle);
//    }
//
//    public void removeApplicationS1811Vehicle(Vehicle vehicle) {
//        applicationS1811Vehicles.removeIf(cf -> cf.getVehicle().equals(vehicle));
//    }
}
