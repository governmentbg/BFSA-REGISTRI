package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.ContactPersonBO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.PlantProductBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s3201")
public class ApplicationS3201 extends BaseApplication {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "activity_addresses")
    private List<AddressBO> activityAddresses = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "activity_types")
    private List<KeyValueDTO> activityTypes = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "plant_plroducts")
    private List<PlantProductBO> plantProducts = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_persons")
    private List<ContactPersonBO> contactPersons = new ArrayList<>();

    @Column(name = "plant_passport_issue")
    private Boolean plantPassportIssue;

    @Column(name = "marking_issue")
    private Boolean markingIssue;
}
