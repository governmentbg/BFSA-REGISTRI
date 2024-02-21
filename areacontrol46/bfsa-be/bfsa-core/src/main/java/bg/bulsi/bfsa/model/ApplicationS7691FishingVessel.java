package bg.bulsi.bfsa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications_s7691_fishing_vessels")
public class ApplicationS7691FishingVessel extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "application_s7691_id")
    private ApplicationS7691 applicationS7691;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id")
    private FishingVessel fishingVessel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownership_type_code")
    private Nomenclature ownershipType;
}
