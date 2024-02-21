package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.KeyValueDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "applications_s7691")
public class ApplicationS7691 extends ApplicationS769 {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "primary_product_food_types", columnDefinition = "json")
    private Set<KeyValueDTO> applicationS7691PrimaryProductFoodTypes = new HashSet<>();

    // ---  Първични продукти ---
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "applications_s7691_primary_product_food_types",
            joinColumns = @JoinColumn(name = "application_s7691_id"),
            inverseJoinColumns = @JoinColumn(name = "primary_product_food_type_code"))
    private Set<Classifier> primaryProductFoodTypes = new HashSet<>();

    @Column(name = "primary_product_food_type_description")
    private String primaryProductFoodTypeDescription;

    @Column(name = "primary_product_live_stock_reg_number")
    private String primaryProductLiveStockRegNumber;

    @Column(name = "primary_product_farmer_identifier")
    private String primaryProductFarmerIdentifier;

    @Column(name = "primary_product_estimated_annual_yield")
    private Double primaryProductEstimatedAnnualYield;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "produced_food_types", columnDefinition = "json")
    private Set<KeyValueDTO> applicationS7691ProducedFoodTypes = new HashSet<>();

    // --- Произведените храни ---
    @Builder.Default
    @ManyToMany
    @JoinTable(name = "applications_s7691_produced_food_types",
            joinColumns = @JoinColumn(name = "application_s7691_id"),
            inverseJoinColumns = @JoinColumn(name = "produced_food_type_code"))
    private Set<Classifier> producedFoodTypes = new HashSet<>();

    @Column(name = "produced_food_type_description")
    private String producedFoodTypeDescription;

    @Column(name = "produced_food_estimated_annual")
    private Double producedFoodEstimatedAnnual; // Прогнозен годишен добив на земеделска продукция

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produced_food_estimated_annual_unit_code")
    private Nomenclature producedFoodEstimatedAnnualUnit;

    // --- Извършвам обработка на дивеч (попълва се само при Вид дейност = "....-чрез ЛОВ) ---
    @Column(name = "hunting_party_branch")
    private String huntingPartyBranch;

    @Column(name = "area_hunting_areas")
    private Double areaHuntingAreas;

    @Column(name = "location_game_processing_facility")
    private String locationGameProcessingFacility;

    @Column(name = "game_station_capacity")
    private Double gameStationCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_station_capacity_unit_code")
    private Nomenclature gameStationCapacityUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_processing_code")
    private Nomenclature gameProcessingPeriod;

    @Column(name = "responsible_people")
    private String responsiblePeople;

    @Column(name = "game_type")
    private String gameType; // Вида на дивеча, от който ще се добива месото

    @Column(name = "permitted_extraction")
    private Double permittedExtraction;

    // --- Данни за риболовните съдове:  (попълва се само при Вид дейност = "....-чрез УЛОВ") ---
    @Builder.Default
    @OneToMany(mappedBy = "applicationS7691", cascade = CascadeType.ALL)
    private List<ApplicationS7691FishingVessel> applicationS7691FishingVessels = new ArrayList<>();

    public void addApplicationS7691FishingVessel(FishingVessel fishingVessel, Nomenclature ownershipType) {
        ApplicationS7691FishingVessel applicationS7691FishingVessel = new ApplicationS7691FishingVessel(this, fishingVessel, ownershipType);
        applicationS7691FishingVessels.add(applicationS7691FishingVessel);
    }

    public void removeApplicationS7691FishingVessel(FishingVessel fishingVessel) {
        applicationS7691FishingVessels.removeIf(fv -> fv.getFishingVessel().equals(fishingVessel));
    }


    // --- Осъществявам директни доставки на първични продукти ---
    @Column(name = "delivery_livestock_reg_number")
    private String deliveryLiveStockRegNumber;

    @Column(name = "delivery_farmer_identifier")
    private String deliveryFarmerIdentifier;

    @Column(name = "delivery_address_livestock_facility")
    private String deliveryAddressLivestockFacility;

    @Column(name = "delivery_facility_capacity")
    private Double deliveryFacilityCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_facility_capacity_unit_code")
    private Nomenclature deliveryFacilityCapacityUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_period_code")
    private Nomenclature deliveryPeriod;

    // --- Данни за капацитет ---
    @Column(name = "food_annual_capacity")
    private Double foodAnnualCapacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_annual_capacity_unit_code")
    private Nomenclature foodAnnualCapacityUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_period_code")
    private Nomenclature foodPeriod;

    // --- Средства за комуникация при търговия от разстояние: (попълва се при "Да") ---
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

}
