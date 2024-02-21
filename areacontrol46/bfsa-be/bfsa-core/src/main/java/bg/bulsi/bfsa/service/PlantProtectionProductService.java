package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.PlantProtectionProductDTO;
import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.PlantProtectionProduct;
import bg.bulsi.bfsa.model.PlantProtectionProductI18n;
import bg.bulsi.bfsa.repository.PlantProtectionProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PlantProtectionProductService {

	private final PlantProtectionProductRepository repository;

	public PlantProtectionProduct findById(final Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(PlantProtectionProduct.class, id));
	}

	public PlantProtectionProduct findByIdOrNull(final Long id) {
		return repository.findById(id).orElse(null);
	}

	public Page<PlantProtectionProduct> findAll(final Pageable pageable) {
		return repository.findAll(pageable);
	}

	@Transactional
	public PlantProtectionProduct create(final PlantProtectionProduct plantProtectionProduct) {
		if (plantProtectionProduct == null) {
			throw new RuntimeException("PlantProtectionProduct is required!");
		}
        return repository.save(plantProtectionProduct);
	}

	@Transactional
	public PlantProtectionProduct update(final Long id, final PlantProtectionProduct plantProtectionProduct, Language language) {
		if (id == null || id <= 0) {
			throw new RuntimeException("ID field is required");
		}
		if (!id.equals(plantProtectionProduct.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		PlantProtectionProduct current = findById(id);
//		BeanUtils.copyProperties(plantProtectionProduct, current);

		current.setQuantity(plantProtectionProduct.getQuantity());

		PlantProtectionProductI18n i18n = current.getI18n(language);
		i18n.setName(plantProtectionProduct.getI18n(language).getName());
		i18n.setActiveSubstances(plantProtectionProduct.getI18n(language).getActiveSubstances());
		i18n.setPurpose(plantProtectionProduct.getI18n(language).getPurpose());
		i18n.setPest(plantProtectionProduct.getI18n(language).getPest());
		i18n.setCrop(plantProtectionProduct.getI18n(language).getCrop());
		i18n.setApplication(plantProtectionProduct.getI18n(language).getApplication());

		return repository.save(current);
	}

	@Transactional(readOnly = true)
    public Page<PlantProtectionProduct> search(final String param, final Language language, final Pageable pageable) {
		return repository.search(param, language.getLanguageId(), pageable);
    }

	@Transactional(readOnly = true)
	public List<PlantProtectionProductDTO> findRevisions(final Long id, Language language) {
		List<PlantProtectionProductDTO> revisions = new ArrayList<>();
		repository.findRevisions(id).get().forEach(r -> {
			PlantProtectionProductDTO rev = PlantProtectionProductDTO.of(r.getEntity(), language);
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


//	@Transactional(readOnly = true)
//	public List<PlantProtectionProduct> findByContractorId(final Long id) {
//		return repository.findByContractorId(id);
//	}
}
