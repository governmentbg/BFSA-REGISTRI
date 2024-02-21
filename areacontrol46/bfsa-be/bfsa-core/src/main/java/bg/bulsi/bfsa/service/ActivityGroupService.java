package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ActivityGroupDTO;
import bg.bulsi.bfsa.dto.BaseVO;
import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.ActivityGroup;
import bg.bulsi.bfsa.model.ActivityGroupI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.repository.ActivityGroupRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ActivityGroupService {

	private final NomenclatureService nomenclatureService;
	private final ActivityGroupRepository repository;

	private final static String ACTIVITY_GROUP_IS_REQUIRED = "ActivityGroup is required!";

	@Transactional(readOnly = true)
	public ActivityGroup findById(final Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(ActivityGroup.class, id));
	}

	@Transactional(readOnly = true)
	public ActivityGroupDTO getById(final Long id, final Language language) {
		return ActivityGroupDTO.of(repository.findById(id).orElseThrow(() -> new EntityNotFoundException(ActivityGroup.class, id)), language);
	}

	@Transactional(readOnly = true)
	public ActivityGroup findByIdOrNull(final Long id) {
		return repository.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public Set<ActivityGroupDTO> findAllParents(Language language) {
		return ActivityGroupDTO.of(repository.findAllByParentIsNull(), language);
	}

	@Transactional(readOnly = true)
	public List<BaseVO> findAllParentsVO(final Language language) {
		return repository.findAllParentsVO(language.getLanguageId());
	}

	@Transactional(readOnly = true)
	public List<BaseVO> findAllByParentIdVO(final Long id, final Language language) {
		return repository.findAllByParentIdVO(id, language.getLanguageId());
	}

	@Transactional
	public ActivityGroup create(final ActivityGroup entity) {
		if (entity == null) {
			throw new RuntimeException(ACTIVITY_GROUP_IS_REQUIRED);
		}

		setNomLists(entity);

		if (!CollectionUtils.isEmpty(entity.getSubActivityGroups())) {
			for (ActivityGroup activityGroup : entity.getSubActivityGroups()) {
				setNomLists(activityGroup);
			}
		}

		return repository.save(entity);
	}

	@Transactional
	public ActivityGroup create(final Long parentId, final ActivityGroup activityGroup) {
		if (parentId == null || parentId <= 0) {
			throw new RuntimeException("ActivityGroup ID is required");
		}
		if (activityGroup == null) {
			throw new RuntimeException(ACTIVITY_GROUP_IS_REQUIRED);
		}

		ActivityGroup parent = findById(parentId);

		setNomLists(activityGroup);

		if (!CollectionUtils.isEmpty(activityGroup.getSubActivityGroups())) {
			for (ActivityGroup g : activityGroup.getSubActivityGroups()) {
				setNomLists(g);
			}
		}

		activityGroup.setParent(parent);
		parent.getSubActivityGroups().add(activityGroup);
		return repository.save(parent);
	}

	@Transactional
	public ActivityGroupDTO update(final Long id, final ActivityGroup entity, Language language) {
		if (id == null || id <= 0) {
			throw new EntityNotFoundException(ActivityGroup.class, id);
		}
		if (!id.equals(entity.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}

		ActivityGroup group = findById(id);
		group.setEnabled(entity.getEnabled());

		ActivityGroupI18n i18n = group.getI18n(language);
		i18n.setName(entity.getI18n(language).getName());
		i18n.setDescription(entity.getI18n(language).getDescription());

		group.getAnimalSpecies().clear();
		for (Nomenclature animalSpices : entity.getAnimalSpecies()) {
			group.getAnimalSpecies().add(nomenclatureService.findByCode(animalSpices.getCode()));
		}

		group.getRemarks().clear();
		for (Nomenclature remark : entity.getRemarks()) {
			group.getRemarks().add(nomenclatureService.findByCode(remark.getCode()));
		}

		group.getRelatedActivityCategories().clear();
		for (Nomenclature rac : entity.getRelatedActivityCategories()) {
			group.getRelatedActivityCategories().add(nomenclatureService.findByCode(rac.getCode()));
		}

		group.getAssociatedActivityCategories().clear();;
		for (Nomenclature rac : entity.getAssociatedActivityCategories()) {
			group.getAssociatedActivityCategories().add(nomenclatureService.findByCode(rac.getCode()));
		}

		return ActivityGroupDTO.of(repository.save(group), language);
	}

	@Transactional(readOnly = true)
	public List<ActivityGroupDTO> findRevisions(final Long id, Language language) {
		List<ActivityGroupDTO> revisions = new ArrayList<>();
		repository.findRevisions(id).get().forEach(r -> {
			ActivityGroupDTO rev = ActivityGroupDTO.of(r.getEntity(), language);
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

	private void setNomLists(ActivityGroup entity) {
		if (!CollectionUtils.isEmpty(entity.getRelatedActivityCategories())) {
			List<String> codes = entity.getRelatedActivityCategories()
					.stream()
					.map(Nomenclature::getCode)
					.toList();
			entity.getRelatedActivityCategories().clear();
			for (String code : codes) {
				entity.getRelatedActivityCategories().add(nomenclatureService.findByCode(code));
			}
		}
		if (!CollectionUtils.isEmpty(entity.getAssociatedActivityCategories())) {
			List<String> codes = entity.getAssociatedActivityCategories()
					.stream()
					.map(Nomenclature::getCode)
					.toList();
			entity.getAssociatedActivityCategories().clear();
			for (String code : codes) {
				entity.getAssociatedActivityCategories().add(nomenclatureService.findByCode(code));
			}
		}
		if (!CollectionUtils.isEmpty(entity.getAnimalSpecies())) {
			List<String> codes = entity.getAnimalSpecies()
					.stream()
					.map(Nomenclature::getCode)
					.toList();
			entity.getAnimalSpecies().clear();
			for (String code : codes) {
				entity.getAnimalSpecies().add(nomenclatureService.findByCode(code));
			}
		}
		if (!CollectionUtils.isEmpty(entity.getRemarks())) {
			List<String> codes = entity.getRemarks()
					.stream()
					.map(Nomenclature::getCode)
					.toList();
			entity.getRemarks().clear();
			for (String code : codes) {
				entity.getRemarks().add(nomenclatureService.findByCode(code));
			}
		}
	}
}
