package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.ServiceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "contractors_facilities")
public class ContractorFacility extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "contractor_id")
    private Contractor contractor;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    @Column(name = "owner")
    private Boolean owner = false;

    @Column(name = "leased_warehouse_space")
    private Boolean leasedWarehouseSpace = false;

    @Column(name = "activity_description")
    private String activityDescription;

    @Column(name = "capacity_usage")
    private Double capacityUsage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_type_code")
    private Nomenclature unitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_type_code")
    private Nomenclature periodType;
}
