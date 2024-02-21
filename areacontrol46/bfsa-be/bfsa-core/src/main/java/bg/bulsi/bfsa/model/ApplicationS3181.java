package bg.bulsi.bfsa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications_s3181")
public class ApplicationS3181 extends BaseApplication {
    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "applications_s3181_facilities",
            joinColumns = @JoinColumn(name = "application_s3181_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id"))
    private List<Facility> facilities = new ArrayList<>();

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    private List<Address> addresses = new ArrayList<>();

    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;

    @Builder.Default
    @OneToMany(mappedBy = "applicationS3181", cascade = CascadeType.ALL)
    private List<ApplicationS3181Vehicle> applicationS3181Vehicles = new ArrayList<>();

    public void addApplicationS3181Vehicle(Vehicle vehicle, Nomenclature ownerType) {
        ApplicationS3181Vehicle applicationS3181Vehicle = new ApplicationS3181Vehicle(this, vehicle, ownerType);
        applicationS3181Vehicles.add(applicationS3181Vehicle);
    }

    public void removeApplicationS3181Vehicle(Vehicle vehicle) {
        applicationS3181Vehicles.removeIf(cf -> cf.getVehicle().equals(vehicle));
    }
}
