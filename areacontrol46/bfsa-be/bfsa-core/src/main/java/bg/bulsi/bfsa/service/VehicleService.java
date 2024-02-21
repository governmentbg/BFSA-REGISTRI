package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.enums.InspectionStatus;
import bg.bulsi.bfsa.enums.InspectionType;
import bg.bulsi.bfsa.enums.VehicleStatus;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Vehicle;
import bg.bulsi.bfsa.model.VehicleI18n;
import bg.bulsi.bfsa.repository.VehicleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VehicleService {

	private final VehicleRepository repository;
	private final BranchService branchService;
	private final ClassifierService classifierService;
	private final InspectionService inspectionService;
	private final UserService userService;
	private final RecordService recordService;
	private final NomenclatureService nomenclatureService;
	private final ContractorService contractorService;

	public Vehicle findById(final Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Vehicle.class, id));
	}

	@Transactional(readOnly = true)
	public VehicleDTO getById(final Long id, final Language language) {
		return VehicleDTO.of(repository.findById(id).orElseThrow(() -> new EntityNotFoundException(Vehicle.class, id)), language);
	}

	public Vehicle findByIdOrNull(final Long id) {
		return repository.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public Page<VehicleDTO> findAll(final Pageable pageable, final Language language) {
		return repository.findAll(pageable).map(f -> VehicleDTO.of(f, language));
	}

	@Transactional
	public Vehicle create(final Vehicle vehicle) {
		if (vehicle == null) {
			throw new RuntimeException("Vehicle is required!");
		}
		vehicle.setBranch(vehicle.getBranch() != null
				? branchService.findById(vehicle.getBranch().getId()) : null);

		if (!CollectionUtils.isEmpty(vehicle.getFoodTypes())) {
			setFoodTypes(vehicle);
		}

        return repository.save(vehicle);
	}

	@Transactional
	public VehicleDTO update(final Long id, final Vehicle vehicle, Language language) {
		if (id == null || id <= 0) {
			throw new RuntimeException("ID field is required");
		}
		if (!id.equals(vehicle.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		Vehicle current = findById(id);
//		BeanUtils.copyProperties(vehicle, current);

		current.setRegistrationPlate(vehicle.getRegistrationPlate());
		current.setEntryNumber(vehicle.getEntryNumber());
		current.setEntryDate(vehicle.getEntryDate());
		current.setCertificateNumber(vehicle.getCertificateNumber());
		current.setCertificateDate(vehicle.getCertificateDate());
		current.setLoad(vehicle.getLoad());
		current.setEnabled(vehicle.getEnabled());

		current.setBranch(vehicle.getBranch() != null && vehicle.getBranch().getId() != null && vehicle.getBranch().getId() > 0
				? branchService.findById(vehicle.getBranch().getId())
				: null);

		VehicleI18n i18n = current.getI18n(language);
		i18n.setBrandModel(vehicle.getI18n(language).getBrandModel());
		i18n.setDescription(vehicle.getI18n(language).getDescription());

		if (!CollectionUtils.isEmpty(vehicle.getFoodTypes())) {
			setFoodTypes(vehicle);
		}

		return VehicleDTO.of(repository.save(current), language);
	}

	@Transactional(readOnly = true)
    public Page<Vehicle> search(final String param, final Language language, final Pageable pageable) {
		return repository.search(param, language.getLanguageId(), pageable);
    }

	public Vehicle findByRegistrationPlate(final String registrationPlate, final Language language) {
		return repository.findByRegistrationPlate(registrationPlate, language.getLanguageId())
		.orElseThrow(() -> new EntityNotFoundException(Vehicle.class, registrationPlate));
	}

	public Vehicle findByRegistrationPlateOrNull(final String registrationPlate, final Language language) {
		return repository.findByRegistrationPlate(registrationPlate, language.getLanguageId()).orElse(null);
	}

	public boolean existsByRegistrationPlate(final String registrationPlate) {
		return repository.existsByRegistrationPlate(registrationPlate);
	}

	public boolean existsByRegistrationPlateAndCertificateNumberIsNotNull(final String registrationPlate) {
		return repository.existsByRegistrationPlateAndCertificateNumberIsNotNull(registrationPlate);
	}

	public Vehicle findByCertificateNumber(final String certificateNumber, final Language language) {
		return repository.findByCertificateNumber(certificateNumber, language.getLanguageId())
				.orElseThrow(() -> new EntityNotFoundException(Vehicle.class, certificateNumber));
	}

	public Vehicle findByCertificateNumberOrNull(final String certificateNumber, final Language language) {
		return repository.findByCertificateNumber(certificateNumber, language.getLanguageId()).orElse(null);
	}

	@Transactional
	public InspectionDTO createInspection(final Long id, final InspectionDTO dto, final Language language) {
		if (dto == null) {
			throw new RuntimeException("Inspection DTO cannot be null.");
		}
		if (!id.equals(dto.getVehicleId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		if (dto.getInspectionType() == null || InspectionType.FOR_APPROVAL.equals(dto.getInspectionType())) {
			throw new RuntimeException("Wrong inspection type: " + dto.getInspectionType());
		}
		if (dto.getContractorId() == null || dto.getContractorId() <= 0) {
			throw new RuntimeException("Contractor ID cannot be null.");
		}

		Vehicle vehicle = findById(dto.getVehicleId());
		Inspection inspection = InspectionDTO.to(dto, language);
		inspection.setVehicle(vehicle);
		inspection.setStatus(InspectionStatus.PROCESSING);

		if (!CollectionUtils.isEmpty(dto.getUsers())) {
			dto.getUsers().forEach(userId -> inspection.getUsers().add(userService.findById(userId)));
		}
		if (dto.getRecordId() != null && dto.getRecordId() > 0) {
			inspection.setRecord(recordService.findById(dto.getRecordId()));
		}
		if (!CollectionUtils.isEmpty(dto.getReasonsCodes())) {
			dto.getReasonsCodes().forEach(
					rc -> inspection.getReasons().add(nomenclatureService.findByCode(rc))
			);
		}
		if (dto.getContractorId() != null && dto.getContractorId() > 0) {
			inspection.setContractor(contractorService.findById(dto.getContractorId()));
		}

		return InspectionDTO.of(inspectionService.create(inspection), language);
	}

	@Transactional
	public InspectionDTO completeInspection(final Long id, final InspectionDTO dto, final Language language) {
		if (dto == null) {
			throw new RuntimeException("Inspection DTO cannot be null.");
		}
		if (!id.equals(dto.getVehicleId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		if (dto.getInspectionType() == null || InspectionType.FOR_APPROVAL.equals(dto.getInspectionType())) {
			throw new RuntimeException("Wrong inspection type: " + dto.getInspectionType());
		}

		Vehicle vehicle = findById(dto.getVehicleId());

		Inspection inspection = vehicle.getInspections().stream()
				.filter(i -> i.getId().equals(dto.getId()))
				.findAny()
				.orElseThrow(() -> new EntityNotFoundException(Inspection.class, dto.getId()));

		if (InspectionStatus.COMPLETED.equals(inspection.getStatus())) {
			throw new RuntimeException("Inspection is already completed.");
		}

		inspection.getI18n(language).setDescription(dto.getDescription());
		inspection.setEndDate(dto.getEndDate());
		inspection.setStatus(InspectionStatus.COMPLETED);
		if (dto.getRiskLevel() != null) {
			inspection.setRiskLevel(dto.getRiskLevel());
		}
		inspection.getReasons().clear();
		if (!CollectionUtils.isEmpty(dto.getReasonsCodes())) {
			dto.getReasonsCodes().forEach(code -> inspection.getReasons().add(nomenclatureService.findByCode(code)));
		}
		inspection.getUsers().clear();
		if (!CollectionUtils.isEmpty(dto.getUsers())) {
			dto.getUsers().forEach(userId -> inspection.getUsers().add(userService.findById(userId)));
		}

		vehicle.setStatus(VehicleStatus.ACTIVE);
//		vehicle.setEnabled(true);

		repository.save(vehicle);

		return InspectionDTO.of(inspection, language);
	}

	private void setFoodTypes(final Vehicle vehicle) {
		List<Classifier> foodTypes = new ArrayList<>();
		vehicle.getFoodTypes()
				.forEach(ft -> foodTypes.add(classifierService.findByCode(ft.getCode())));
		vehicle.getFoodTypes().clear();
		vehicle.getFoodTypes().addAll(foodTypes);
	}

	@Transactional
	public VehicleDTO updateStatus(final Long id, final KeyValueDTO dto, Language language) {
		if (id == null || id <= 0) {
			throw new RuntimeException("ID field is required");
		}
		if (!id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		Vehicle vehicle = findById(id);
		vehicle.setStatus(VehicleStatus.valueOf(dto.getStatus()));
		return VehicleDTO.of(repository.save(vehicle), language);
	}

}
