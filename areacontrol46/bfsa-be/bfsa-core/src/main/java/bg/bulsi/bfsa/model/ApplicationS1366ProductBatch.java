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

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s1366_product_batches")
public class ApplicationS1366ProductBatch extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "application_s1366_product_id")
    private ApplicationS1366Product applicationS1366Product;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "per_unit_net_weight")
    private Double perUnitNetWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_unit_net_weight_unit_code")
    private Nomenclature perUnitNetWeightUnit;

    @Column(name = "batch_net_weight")
    private Double batchNetWeight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_net_weight_unit_code")
    private Nomenclature batchNetWeightUnit;

    @Column(name = "units_in_batch")
    private Integer unitsInBatch;
}
