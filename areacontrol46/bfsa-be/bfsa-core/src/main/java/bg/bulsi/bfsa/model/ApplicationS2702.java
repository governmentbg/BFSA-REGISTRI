package bg.bulsi.bfsa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.NotAudited;
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
@Table(name = "applications_s2702")
public class ApplicationS2702 extends BaseApplication {

    //    --- Данни на лицето/лицата, отговарящо/и за дейността ---
    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Contractor> activityResponsiblePersons = new ArrayList<>();

    //   --- ДАННИ ЗА МАТЕРИАЛА, ЗА КОЙТО СЕ ЗАЯВЯВА ВРЕМЕННО РАЗРЕШЕНИЕ ---
    //   --- Вид на материала (текст) ---
    @Column(name = "material_type")
    private String materialType;

    //    --- Научно наименование на  материала / публикувани източници  (текст) ---
    @Column(name = "material_name")
    private String materialName;

    //    --- Общо количество на материала, който ще се въвежда/движи (число) ---
    @Column(name = "material_total_amount")
    private Double materialTotalAmount;

    //    --- мерна единица (избор от номенлатура) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_measuring_unit_code", referencedColumnName = "code")
    private Nomenclature materialMeasuringUnitCode;

    //    --- Списък с  всички въвеждания/движения: ---
//    @Column(name = "material_movements")
//    private String materialMovements;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Double> materialMovements = new ArrayList<>();

    //    --- Общо количество на материала, който ще се въвежда/движи (число) ---
    @Column(name = "material_movement_summary")
    private String materialMovementSummary;

    //    --- Страна на произход (избор от номенклатура) ---
    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_origin_country_code")
    private Country materialOriginCountry;

    //    --- Страна на износ (ако е различна от страна на произход)(избор от номенклатура) ---
    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_export_country_code")
    private Country materialExportCountry;

    //    --- Условия на опаковане на материала (текст) ---
    @Column(name = "material_packaging_condition")
    private String materialPackagingCondition;

    //    --- ДАННИ НА ИЗНОСИТЕЛ/ИЗПРАЩАЧ/ДОСТАВЧИК ---
    //    --- Вид дейност (избор от номенклатура) ---
//    @OneToOne
//    @JoinColumn(name = "supplier_activity_type_code", referencedColumnName = "code")
//    private Nomenclature supplierActivityType;

    //    --- Име (текст); Презиме (текст); Фамилия (текст); ЕГН (ЕГН) ---
    //    --- Наименование на  фирмата/организацията (текст) ---
    //    --- Държава (избор от номенклатура) ---
    //    --- Населено място (текст); Адрес (текст) ---
    //    --- Телефон (текст); Имейл (текст) ---
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Contractor supplier;

    //    --- ДАННИ ЗА КАРАНТИННИЯ ПУНКТ/СЪОРЪЖЕНИЕТО ЗА ЗАДЪРЖАНЕ,
    //    ОДОБРЕНО СЪГЛАСНО ИЗИСКВАНИЯТА НА ЧЛ. 61 ОТ РЕГЛАМЕНТ (ЕС) 2016/2031 ---
    //    --- Наименование (текст) ---
    @Column(name = "quarantine_station_name")
    private String quarantineStationName;

    //    --- Описание (текст) ---
    @Column(name = "quarantine_station_description")
    private String quarantineStationDescription;

    //    --- Област (избор от номенклатури); Община (избор от номенклатури) ---
    //    --- Населено място (избор от номенклатури); Адрес (текст) ---
    @Column(name = "quarantine_station_address")
    private String quarantineStationAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address supplierAddress;

    //    --- Данни на лицето, отговарящо за карантинният пункт или съоръжението за задържане ---
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "quarantine_station_person_id")
    private Contractor quarantineStationPerson;

    //    --- С какви мерки ще се гарантира сигурното и безопасно съхранение на материала по време на дейността,
    //    за която се заявява: (текст) ---
    @Column(name = "quarantine_station_material_storage_measure")
    private String quarantineStationMaterialStorageMeasure;

    //    --- Резюме на естеството и целите на заявяваната дейност (текст) ---
    @Column(name = "requested_activity_summary")
    private String requestedActivitySummary;

    //    --- ПРОДЪЛЖИТЕЛНОСТ НА ЗАЯВЯВАНАТА ДЕЙНОСТ ---
    //    --- Дата на първо въвеждане/движение (дата) ---
    @Column(name = "first_entry_date")
    private OffsetDateTime firstEntryDate;

    //    --- Дата на очаквано приключване на дейността (дата) ---
    @Column(name = "expected_completion_date")
    private OffsetDateTime expectedCompletionDate;

    //    --- Крайно използване на материала: (избор от номенклатура) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_end_use_code", referencedColumnName = "code")
    private Nomenclature materialEndUse;

    //    --- Метод на унищожаване или третиране на материала след приключване на дейността: (текст) ---
    @Column(name = "material_destruction_method")
    private String materialDestructionMethod;

    //    --- С какви мерки ще се гарантира безопасното отлагане/третиране на материала
    //    след приключване на дейността: (текст) ---
    @Column(name = "material_safe_measure")
    private String materialSafeMeasure;

    //    --- Заявителят предоставя и друга информация и разяснения при поискване от компетентния орган,
    //    до когото е отправено заявлението. ---
    @Column(name = "description")
    private String description;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "record_id")
//    private Record record;


}


