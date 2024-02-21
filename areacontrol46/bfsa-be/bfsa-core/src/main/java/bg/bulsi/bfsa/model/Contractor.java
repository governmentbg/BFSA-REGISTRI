package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.enums.ServiceType;
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
@Table(name = "contractors")
public class Contractor extends BaseEntity {

	@Column(name = "email")
	private String email;

	@Column(name = "phone")
	private String phone;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "degree")
	private String degree;

	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "identifier", nullable = false, unique = true)
	private String identifier;

	@Enumerated(EnumType.STRING)
	@Column(name = "entity_type")
	private EntityType entityType;

	// Nomenclature-library 03200: Contractor Activity Type
	@ManyToOne//(fetch = FetchType.LAZY)
	@JoinColumn(name = "contractor_activity_type_code", referencedColumnName = "code")
	private Nomenclature contractorActivityType;

	@Column(name = "enabled")
	private Boolean enabled;

	@Column(name = "farmer")
	private Boolean farmer;

	@NotAudited
	@Builder.Default
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinTable(name = "contractors_roles", joinColumns = @JoinColumn(name = "contractor_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@NotAudited
	@Builder.Default
	@OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL)
	private List<Task> tasks = new ArrayList<>();

	@NotAudited
	@Builder.Default
	@OneToMany(mappedBy = "applicant")
	private List<Record> records = new ArrayList<>();

	@NotAudited
	@Builder.Default
	@OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ContractorRelation> contractorRelations = new ArrayList<>();

	@Builder.Default
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "contractors_addresses", joinColumns = @JoinColumn(name = "contractor_id"),
			inverseJoinColumns = @JoinColumn(name = "address_id"))
	private List<Address> addresses = new ArrayList<>();

	@NotAudited
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id") // , nullable = false
	private Branch branch;

	@NotAudited
	@Builder.Default
	@OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ContractorPaper> contractorPapers = new ArrayList<>();

	@ManyToMany
	@Builder.Default
	@JoinTable(name = "contractors_registers", joinColumns = @JoinColumn(name = "contractor_id"),
			inverseJoinColumns = @JoinColumn(name = "code"))
	private List<Classifier> registers = new ArrayList<>();

	@NotAudited
	@Builder.Default
	@OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL)
	private List<ContractorFacility> contractorFacilities = new ArrayList<>();

	public void addContractorRelation(Contractor contractor, ServiceType serviceType, Nomenclature ownerType) {
		ContractorRelation contractorRelation = new ContractorRelation(contractor, this, serviceType, ownerType);
		contractor.getContractorRelations().add(contractorRelation);
//		contractorVehicles.add(contractorVehicle);
	}

	public void addContractorFacility(Facility facility, ServiceType serviceType) {
		addContractorFacility(facility, serviceType, null, null);
	}

	public void addContractorFacility(Facility facility, ServiceType serviceType, Boolean owner) {
		addContractorFacility(facility, serviceType, owner, null);
	}

	public void addContractorFacility(Facility facility, ServiceType serviceType, Boolean owner, Boolean leasedWarehouseSpace) {
		ContractorFacility contractorFacility = new ContractorFacility(this, facility, serviceType, owner, leasedWarehouseSpace,
				null, null, null, null);
		contractorFacilities.add(contractorFacility);
//		facility.getContractorFacilities().add(contractorFacility);
	}

	public void addContractorFacility(Facility facility, ServiceType serviceType, Boolean owner, Boolean leasedWarehouseSpace,
									  String activityDescription, Double capacityUsage, Nomenclature unitType, Nomenclature periodType) {
		ContractorFacility contractorFacility = new ContractorFacility(this, facility, serviceType, owner, leasedWarehouseSpace,
				activityDescription, capacityUsage, unitType, periodType);
		contractorFacilities.add(contractorFacility);
//		facility.getContractorFacilities().add(contractorFacility);
	}

	public void removeContractorFacility(Facility facility) {
		contractorFacilities.removeIf(cf -> cf.getFacility().equals(facility));
//		facility.getContractorFacilities().removeIf(cf -> cf.getContractor().equals(this));
	}

	@NotAudited
	@Builder.Default
	@OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL)
	private List<ContractorVehicle> contractorVehicles = new ArrayList<>();

	public void addContractorVehicle(Vehicle vehicle, ServiceType serviceType, Nomenclature ownerType) {
		ContractorVehicle contractorVehicle = new ContractorVehicle(this, vehicle, serviceType, ownerType);
		contractorVehicles.add(contractorVehicle);
//		vehicle.getContractorVehicles().add(contractorVehicle);
	}

	public void removeContractorVehicle(Vehicle vehicle) {
		contractorVehicles.removeIf(cf -> cf.getVehicle().equals(vehicle));
//		vehicle.getContractorVehicles().removeIf(cf -> cf.getContractor().equals(this));
	}

	@NotAudited
	@Builder.Default
	@OneToMany(mappedBy = "contractor", cascade = CascadeType.ALL)
	private List<ContractorFishingVessel> contractorFishingVessels = new ArrayList<>();

	public void addContractorFishingVessel(FishingVessel fishingVessel, ServiceType serviceType, Nomenclature ownerType) {
		ContractorFishingVessel contractorFishingVessel = new ContractorFishingVessel(this, fishingVessel, serviceType, ownerType);
		contractorFishingVessels.add(contractorFishingVessel);
	}

	public void removeContractorFishingVessel(FishingVessel fishingVessel) {
		contractorFishingVessels.removeIf(fv -> fv.getFishingVessel().equals(fishingVessel));
	}
}
