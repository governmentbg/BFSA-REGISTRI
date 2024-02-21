package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.DistantNeighborSettlementBO;
import jakarta.persistence.CascadeType;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s2695_field")
public class ApplicationS2695Field extends BaseApplication {

    @Column(name = "treatment_date")
    private OffsetDateTime treatmentDate;

    @Column(name = "treatment_start_hour")
    private OffsetDateTime treatmentStartHour;

    @Column(name = "treatment_end_hour")
    private OffsetDateTime treatmentEndHour;

    @Column(name = "treatment_area")
    private Double treatmentArea;

    @Column(name = "treatment_distance")
    private Double treatmentDistance;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address treatmentAddress;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "distant_neighbor_settlements")
    private List<DistantNeighborSettlementBO> distantNeighborSettlements;
}