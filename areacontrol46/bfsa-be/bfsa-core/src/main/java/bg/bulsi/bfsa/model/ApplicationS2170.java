package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.BioCharacteristicBO;
import bg.bulsi.bfsa.dto.CropBO;
import bg.bulsi.bfsa.dto.IngredientBO;
import bg.bulsi.bfsa.dto.MaterialBO;
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
import org.hibernate.envers.NotAudited;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s2170")
public class ApplicationS2170 extends BaseApplication {

    //    1. Име на лицето /търговеца, пускащо продукта на пазара:
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Contractor supplier;

    //    2. Идентифициране на производителя
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    private Contractor manufacturer;

    //    2.2. Място на производство
    @JdbcTypeCode(SqlTypes.JSON)
    @JoinColumn(name = "manufacture_address")
    private AddressBO manufactureAddress;

    @Column(name = "manufacture_place")
    private String manufacturePlace;

    //    3.  Идентификация на продукта
    @Column(name = "name")
    private String name;

    @Column(name = "name_lat")
    private String nameLat;

    //    3.2. Категория на продукта
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_category_code", referencedColumnName = "code")
    private Nomenclature productCategory;

    //    3.3. Вид на продукта  (избор от номенклатура според категорията)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_code", referencedColumnName = "code")
    private Nomenclature productType;

    //    Държава членка на ЕС, в която е пуснат на пазара
    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eu_market_placement_country_code")
    private Country euMarketPlacementCountry;

    //    4. Суровини:
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "materials")
    private List<MaterialBO> materials = new ArrayList<>();

    //    5. Производствена технология
    @Column(name = "processing_description")
    private String processingDescription;

    @Column(name = "processing_description_patent_number")
    private String processingDescriptionPatentNumber;

    //    6. Готов продукт
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ingredients")
    private List<IngredientBO> ingredients = new ArrayList<>();

    //    6.2 Физични и физико-химични характеристики:
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physical_state_code", referencedColumnName = "code")
    private Nomenclature physicalStateCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physical_code", referencedColumnName = "code")
    private Nomenclature physicalCode;

    @Column(name = "dry_substance")
    private Double drySubstance;

    @Column(name = "organic_substance")
    private Double organicSubstance;

    @Column(name = "inorganic_substance")
    private Double inorganicSubstance;

    @Column(name = "ph")
    private Double ph;

    //    6.3. Съдържание на тежки метали в мг/кг - количество в готовия продукт
    @Column(name = "arsen")
    private Double arsen;

    @Column(name = "nickel")
    private Double nickel;

    @Column(name = "cadmium")
    private Double cadmium;

    @Column(name = "mercury")
    private Double mercury;

    @Column(name = "chrome")
    private Double chrome;

    @Column(name = "lead")
    private Double lead;

    //    6.4. Биологични и/или биохимични характеристики
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "living_organisms")
    private List<BioCharacteristicBO> livingOrganisms = new ArrayList<>();

    //    7. Микробиологична характеристика за липса на вредни микроорганизми
    @Column(name = "enterococci")
    private Double enterococci;

    @Column(name = "enterococci_coli")
    private Double enterococciColi;

    @Column(name = "clostridium_perfringens")
    private Double clostridiumPerfringens;

    @Column(name = "salmonella")
    private Double salmonella;

    @Column(name = "staphylococus")
    private Double staphylococus;

    @Column(name = "aspergillus")
    private Double aspergillus;

    @Column(name = "nematodes")
    private Double nematodes;

    //    8. Очакван ефект:
    @Column(name = "expected_effect")
    private String expectedEffect;

    //    9. Култури, дози, начин на употреба
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "crops")
    private Set<CropBO> crops = new HashSet<>();

    //    10. Специални предпазни мерки
    @Column(name = "possible_mixes")
    private String possibleMixes;

    @Column(name = "not_recommended_mixes")
    private String notRecommendedMixes;

    @Column(name = "not_recommended_climatic_conditions")
    private String notRecommendedClimaticConditions;

    @Column(name = "not_recommended_soil_conditions")
    private String notRecommendedSoilConditions;

    @Column(name = "prohibited_import_crops")
    private String prohibitedImportCrops;

    //    11.  Предпазни мерки при манипулация
    @Column(name = "storage")
    private String storage;

    @Column(name = "transport")
    private String transport;

    @Column(name = "fire")
    private String fire;

    @Column(name = "human_accident")
    private String humanAccident;

    @Column(name = "spilliage_accident")
    private String spilliageAccident;

    @Column(name = "handling_deactivation_option")
    private String handlingDeactivationOption;
}

