package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.enums.VehicleStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {

	@Column(name = "registration_plate", unique = true)
	private String registrationPlate;
	@Column(name = "entry_number", unique = true)
	private String entryNumber;
	@Column(name = "entry_date")
	private LocalDate entryDate;
	@Column(name = "certificate_number")
	private String certificateNumber;
	@Column(name = "certificate_date")
	private LocalDate certificateDate;
	@Column(name = "enabled")
	private Boolean enabled;
	@Column(name = "license_number")
	private String licenseNumber;

	// Обем (за цистерни и контейнери) (число) - литри
	@Column(name = "volume")
	private Double volume;
	// Товароподемност (число) - кг
	@Column(name = "load")
	private Double load;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private VehicleStatus status;

	// In which classifier is registered
	@ManyToMany
	@Builder.Default
	@JoinTable(name = "vehicles_registers", joinColumns = @JoinColumn(name = "vehicle_id"),
			inverseJoinColumns = @JoinColumn(name = "code"))
	private List<Classifier> registers = new ArrayList<>();

	// Nomenclature-library 01700: Vehicle Type
	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_type_code")
	private Nomenclature vehicleType;

	@NotAudited
	@Builder.Default
	@ManyToMany
	@JoinTable(
			name = "vehicles_food_types",
			joinColumns = @JoinColumn(name="vehicle_id"),
			inverseJoinColumns = @JoinColumn(name="food_type_code")
	)
	private Set<Classifier> foodTypes = new HashSet<>();

	@NotAudited
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="branch_id")
	private Branch branch;

	@NotAudited
	@Builder.Default
	@OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL)
	private List<Inspection> inspections = new ArrayList<>();

//	@NotAudited
//	@OneToMany(mappedBy = "vehicle")
//	private List<ContractorVehicle> contractorVehicles = new ArrayList<>();

	public void addContractorVehicle(Contractor contractor, ServiceType serviceType, Nomenclature ownerType) {
		ContractorVehicle contractorVehicle = new ContractorVehicle(contractor, this, serviceType, ownerType);
		contractor.getContractorVehicles().add(contractorVehicle);
//		contractorVehicles.add(contractorVehicle);
	}

	public void removeContractorVehicle(Contractor contractor) {
//		contractorVehicles.removeIf(cf -> cf.getContractor().equals(contractor));
		contractor.getContractorVehicles().removeIf(cf -> cf.getVehicle().equals(this));
	}

	@Builder.Default
	@OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private Set<VehicleI18n> i18ns = new HashSet<>();

	public VehicleI18n getI18n(final Language language) {
		if (language != null && StringUtils.hasText(language.getLanguageId()) && !CollectionUtils.isEmpty(i18ns)) {
			for (VehicleI18n i18n : i18ns) {
				if (language.getLanguageId().equals(i18n.getVehicleI18nIdentity().getLanguageId())) {
					return i18n;
				}
			}
		}
		VehicleI18n i18n = new VehicleI18n(this, language);
		i18ns.add(i18n);
		return i18n;
	}
}
