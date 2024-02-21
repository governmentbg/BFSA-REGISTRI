package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.AddressDTO;
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
@Table(name = "applications_s3125")
public class ApplicationS3125 extends BaseApplication {
    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;

    // Nomenclature-library 02200: Operator Type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_type_code", nullable = false)
    private Nomenclature applicantType;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL)
    private List<FoodSupplement> foodSupplements = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "distance_Trading_Address")
    private AddressDTO distanceTradingAddress;
}
