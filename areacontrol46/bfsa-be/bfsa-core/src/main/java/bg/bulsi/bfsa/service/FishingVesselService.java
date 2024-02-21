package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.FishingVesselDTO;
import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.FishingVessel;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.FishingVesselRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FishingVesselService {

	private final FishingVesselRepository repository;
	private final BranchService branchService;
	private final NomenclatureService nomenclatureService;

	private final static String REQUIRED_ERROR = "Fishing vessel is required!";

	@Transactional(readOnly = true)
	public Page<FishingVesselDTO> findAll(final Pageable pageable, final Language language) {
		return repository.findAll(pageable).map(f -> FishingVesselDTO.of(f, language));
	}

	public FishingVessel findById(final Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(FishingVessel.class, id));
	}

	public FishingVesselDTO getById(final Long id, final Language language) {
		return FishingVesselDTO.of(findByIdOrNull(id), language);
	}

	public FishingVessel findByIdOrNull(final Long id) {
		return repository.findById(id).orElse(null);
	}

	public FishingVessel findByRegNumber(final String regNumber) {
		return repository.findByRegNumber(regNumber);
	}

	public FishingVessel findByExternalMarking(final String externalMarking) {
		return repository.findByExternalMarking(externalMarking);
	}

	public Page<FishingVessel> findByHullLength(final Double hullLength, Pageable pageable) {
		return repository.findByHullLength(hullLength, pageable);
	}

	public FishingVessel findByEntryNumber(final String entryNumber) {
		return repository.findByEntryNumber(entryNumber);
	}

	public Page<FishingVessel> findByDate(final LocalDate from, final LocalDate to, final Pageable pageable) {
		return repository.findByDate(from, to, pageable);
	}

	public Page<FishingVessel> findByBranch(final Long branchId, Pageable pageable) {
		return repository.findByBranch_Id(branchId, pageable);
	}

	public Page<FishingVessel> findByAssignmentType(String assignmentTypeCode, Pageable pageable) {
		return repository.findByAssignmentType_Code((assignmentTypeCode), pageable);
	}

	//TODO Use entity dto instead for input parameter
	@Transactional
	public FishingVessel create(final FishingVesselDTO dto) {
		if (dto == null) {
			throw new RuntimeException(REQUIRED_ERROR);
		}

		FishingVessel fishingVessel = FishingVesselDTO.to(dto);

		if (dto.getBranchId() != null && dto.getBranchId() > 0) {
			fishingVessel.setBranch(branchService.findById(dto.getBranchId()));
        }
		if (StringUtils.hasText(dto.getAssignmentTypeCode())) {
			fishingVessel.setAssignmentType(nomenclatureService.findByCode(dto.getAssignmentTypeCode()));
		}

		return repository.save(fishingVessel);
	}

	@Transactional
	public FishingVesselDTO update(final Long id, final FishingVesselDTO dto, final Language language) {
		if (id == null || id <= 0 || dto == null || !id.equals(dto.getId())) {
			throw new EntityNotFoundException(FishingVessel.class, id);
		}

		FishingVessel fishingVessel = findById(id);
		BeanUtils.copyProperties(dto, fishingVessel);

		if (dto.getBranchId() != null && dto.getBranchId() > 0) {
			fishingVessel.setBranch(branchService.findById(dto.getBranchId()));
		}
		if (StringUtils.hasText(dto.getAssignmentTypeCode())) {
			fishingVessel.setAssignmentType(nomenclatureService.findByCode(dto.getAssignmentTypeCode()));
		}

		return FishingVesselDTO.of(repository.save(fishingVessel), language);
	}

	@Transactional(readOnly = true)
	public List<FishingVesselDTO> findRevisions(final Long id, final Language language) {
		List<FishingVesselDTO> revisions = new ArrayList<>();
		repository.findRevisions(id).get().forEach(r -> {
			FishingVesselDTO rev = FishingVesselDTO.of(r.getEntity(), language);
			rev.setRevisionMetadata(RevisionMetadataDTO.builder()
					.revisionNumber(r.getMetadata().getRevisionNumber().orElse(null))
					.revisionInstant(r.getMetadata().getRevisionInstant().orElse(null))
					.revisionType(r.getMetadata().getRevisionType().name())
					.createdBy(r.getEntity().getCreatedBy())
					.createdDate(r.getEntity().getCreatedDate())
					.lastModifiedBy(r.getEntity().getLastModifiedBy())
					.lastModifiedDate(r.getEntity().getLastModifiedDate())
					.build()
			);
			revisions.add(rev);
		});
		return revisions;
	}
}
