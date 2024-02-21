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
import jakarta.persistence.OneToOne;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ЗАЯВЛЕНИЕ за регистрация на обекти за производство и търговия с храни
 * Това е базово заявление. Съответните разновидности са:
 * <p>2. Търговия на ДРЕБНО и заведения за обществено хранене</p>
 */

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s7692")
public class ApplicationS7692 extends BaseApplication {

    // Данни за обекта;
    // Допълнителна информация за обекта;
    // Групи храни, които ще се произвеждат, преработват и/или дистрибутират в обекта * (избор от номенклатура с чек бокс)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    // Пояснение към групи храни (текст)
    @Column(name = "food_type_description")
    private String foodTypeDescription;

    // Средства за комуникация при търговия от разстояние
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "remote_trading_address", referencedColumnName = "id")
    private Address remoteTradingAddress;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "food_types", columnDefinition = "json")
    private Set<KeyValueDTO> applicationS7692FoodTypes = new HashSet<>();

    @NotAudited
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "applications_s7692_food_types",
            joinColumns = @JoinColumn(name = "applications_s7692_id"),
            inverseJoinColumns = @JoinColumn(name = "food_type_code")
    )
    private Set<Classifier> foodTypes = new HashSet<>();


    // Използвам МПС за транспортиране на храни по чл. 52
    // Описание на превозните средства по чл.52, които ще се използват за транспортиране на храни:  (попълва се при "Да")
    // Използвам МПС за транспортиране на храни по чл. 50
    @Builder.Default
    @OneToMany(mappedBy = "applicationS7692", cascade = CascadeType.ALL)
    private List<ApplicationS7692Vehicle> applicationS7692Vehicles = new ArrayList<>();

    // Дата на започване на дейността *   (дата)
    @Column(name = "commencement_activity_date")
    private OffsetDateTime commencementActivityDate;

    public void addApplicationS7692Vehicle(Vehicle vehicle, Nomenclature ownerType) {
        ApplicationS7692Vehicle applicationS7692Vehicle = new ApplicationS7692Vehicle(this, vehicle, ownerType);
        applicationS7692Vehicles.add(applicationS7692Vehicle);
    }

    public void removeApplicationS7692Vehicle(Vehicle vehicle) {
        applicationS7692Vehicles.removeIf(cf -> cf.getVehicle().equals(vehicle));
    }
}
