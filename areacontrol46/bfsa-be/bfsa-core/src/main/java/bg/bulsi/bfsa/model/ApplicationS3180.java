package bg.bulsi.bfsa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "applications_s3180")
public class ApplicationS3180 extends BaseApplication {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    // Средства за комуникация при търговия от разстояние
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;

    @Builder.Default
    @OneToMany(mappedBy = "applicationS3180", cascade = CascadeType.ALL)
    private List<ApplicationS3180Vehicle> applicationS3180Vehicles = new ArrayList<>();

    public void addApplicationS3180Vehicle(Vehicle vehicle, Nomenclature ownerType) {
        ApplicationS3180Vehicle applicationS3180Vehicle = new ApplicationS3180Vehicle(this, vehicle, ownerType);
        applicationS3180Vehicles.add(applicationS3180Vehicle);
    }

    public void removeApplicationS3180Vehicle(Vehicle vehicle) {
        applicationS3180Vehicles.removeIf(cf -> cf.getVehicle().equals(vehicle));
    }
}