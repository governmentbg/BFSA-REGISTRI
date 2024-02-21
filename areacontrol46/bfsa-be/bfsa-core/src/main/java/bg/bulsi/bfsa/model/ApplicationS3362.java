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

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@Table(name = "applications_s3362")
public class ApplicationS3362 extends OrderApplication {

    @Column(name = "registration_date")
    private OffsetDateTime registrationDate;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "contract_start_date")
    private OffsetDateTime contractStartDate;

    @Column(name = "contract_end_date")
    private OffsetDateTime contractEndDate;

    @Column(name = "ppp_name")
    private String pppName;

    @Column(name = "ppp_manufacturer_name")
    private String pppManufacturerName;

    @Column(name = "ppp_package_volume")
    private Double pppPackageVolume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppp_package_unit_code", referencedColumnName = "code")
    private Nomenclature pppPackageUnitCode;

    @Column(name = "ppp_package_material")
    private String pppPackageMaterial;

    @Column(name = "package_volume")
    private Double packageVolume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_unit_code", referencedColumnName = "code")
    private Nomenclature packageUnitCode;

    @Column(name = "package_material")
    private String packageMaterial;
}
