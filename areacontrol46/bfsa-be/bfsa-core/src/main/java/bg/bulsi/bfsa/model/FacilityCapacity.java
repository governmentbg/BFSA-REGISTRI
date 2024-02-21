package bg.bulsi.bfsa.model;

import jakarta.persistence.Column;
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
@Table(name = "facilities_capacities")
public class FacilityCapacity extends BaseEntity {

    @Column(name = "product")
    private String product;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_code")
    private Nomenclature material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_code")
    private Nomenclature unit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    //(избор от номенклатура - Nomenclature-library 02900)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_milk_type_code")
    private Nomenclature rawMilkType;

    @Column(name = "fridge_capacity")
    private Double fridgeCapacity;
}
