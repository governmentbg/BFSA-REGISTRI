package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.TreatmentBO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@Table(name = "applications_s3363")
public class ApplicationS3363 extends OrderApplication {

    @Column(name = "registration_date")
    private OffsetDateTime registrationDate;

    @Column(name = "seed_name")
    private String seedName;

    @Column(name = "seed_quantity")
    private String seedQuantity;

    @Column(name = "seed_trade_name")
    private String seedTradeName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "treatments", columnDefinition = "jsonb")
    private List<TreatmentBO> treatments = new ArrayList<>();

    @Column(name = "ppp_manufacturer_name")
    private String pppManufacturerName;

    @Column(name = "ppp_importer_name")
    private String pppImporterName;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Country> countries = new HashSet<>();

    @Column(name = "certificate_number")
    private String certificateNumber;

    @Column(name = "certificate_date")
    private OffsetDateTime certificateDate;

    @Column(name = "certificate_until_date")
    private OffsetDateTime certificateUntilDate;
}
