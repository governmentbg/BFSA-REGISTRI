package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.MaintenanceEquipmentBO;
import bg.bulsi.bfsa.dto.PersonBO;
import bg.bulsi.bfsa.dto.TestMethodologyBO;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications_s502")
public class ApplicationS502 extends BaseApplication {

    @Column(name = "price", precision = 7, scale = 2)
    private BigDecimal price;

    //    Mеста за изпитване
//    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "testing_addresses")
    private List<AddressBO> testingAddresses = new ArrayList<>();

    //    Искано одобрение по чл. 23, ал. 1 от Наредба № 19 за първоначално одобряване на база за следната/ните групи култури (избор от номенклатура с чек бокс)
    @Builder.Default
    @ManyToMany//(mappedBy = "applications_s502", fetch = FetchType.EAGER)
    private List<Nomenclature> plantGroupTypes = new ArrayList<>();

    //    Списък на хората, работещи в изпитването на ПРЗ - функции и отговорности.
//    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ppp_testing_persons")
    private List<PersonBO> pppTestingPersons = new ArrayList<>();

    //    Списък на притежаваните или взети под наем съоръжения /сгради, оранжерии и др.
//    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "eased_facilities")
    private List<String> easedFacilities;

    //    Оборудване - поддръжка и калибриране.
//    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "maintenance_equipments")
    private List<MaintenanceEquipmentBO> maintenanceEquipments = new ArrayList<>();

//    5. 3. 1. Списък на всички Стандартни оперативни процедури, свързани с опити за ефикасност
//    TODO: Implement file upload.
//    standardOperatingProcedures
//    standardOperatingProcedureFileName
//    standardOperatingProcedureDescription

    //    Методики на изпитване
//    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "test_methodologies")
    private List<TestMethodologyBO> testMethodologies = new ArrayList<>();

    //    Планове за изследване, организиране на опит, събиране на сурови данни, доклади (текст)
    private String researchPlansDescription;

    //    Организация на архивиране на документацията от изпитването (текст)
    private String archivingDocDescription;
}
