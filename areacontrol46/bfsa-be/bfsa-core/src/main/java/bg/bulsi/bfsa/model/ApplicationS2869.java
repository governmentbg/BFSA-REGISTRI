package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.FacilityDTO;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s2869")
public class ApplicationS2869 extends BaseApplication {

    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_50_vehicles")
    private List<VehicleDTO> ch50vehicles = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_52_vehicles")
    private List<VehicleDTO> ch52vehicles = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "facilities")
    private List<FacilityDTO> facilities = new ArrayList<>();

//    @Builder.Default
//    @ManyToMany(cascade = CascadeType.ALL)
//    private List<Facility> facilities = new ArrayList<>();
//
//    @Builder.Default
//    @ManyToMany
//    private List<Nomenclature> activities = new ArrayList<>();

}
