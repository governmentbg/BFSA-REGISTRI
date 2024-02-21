package bg.bulsi.bfsa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications_s3182")
public class ApplicationS3182 extends BaseApplication {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;

    @Builder.Default
    @OneToMany(mappedBy = "applicationS3182", cascade = CascadeType.ALL)
    private List<ApplicationS3182Vehicle> applicationS3182Vehicles = new ArrayList<>();

    public void addApplicationS3182Vehicle(Vehicle vehicle, Nomenclature ownerType) {
        ApplicationS3182Vehicle applicationS3182Vehicle = new ApplicationS3182Vehicle(this, vehicle, ownerType);
        applicationS3182Vehicles.add(applicationS3182Vehicle);
    }

    public void removeApplicationS3182Vehicle(Vehicle vehicle) {
        applicationS3182Vehicles.removeIf(cf -> cf.getVehicle().equals(vehicle));
    }
}