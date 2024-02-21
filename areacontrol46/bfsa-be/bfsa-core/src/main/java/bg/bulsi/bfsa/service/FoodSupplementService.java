package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.FoodSupplementDTO;
import bg.bulsi.bfsa.dto.RevisionMetadataDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.FoodSupplement;
import bg.bulsi.bfsa.model.FoodSupplementI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.FoodSupplementRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FoodSupplementService {

	private final FoodSupplementRepository repository;
	private final ContractorService contractorService;
	private final CountryService countryService;
	private final NomenclatureService nomenclatureService;

	public FoodSupplement findById(final Long id) {
		return repository.findById(id).orElseThrow(() -> new EntityNotFoundException(FoodSupplement.class, id));
	}

	public FoodSupplement findByIdOrNull(final Long id) {
		return repository.findById(id).orElse(null);
	}

	public Page<FoodSupplement> findAll(final Pageable pageable) {
		return repository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Page<FoodSupplementDTO> findAllByApplicantId(final Long applicantId, Language language, final Pageable pageable) {
		Page<FoodSupplement> p = repository.findAllByApplicant_IdAndEnabledIsTrueAndI18ns_foodSupplementI18nIdentity_languageId(applicantId, language.getLanguageId(), pageable);
		return p.map(f -> FoodSupplementDTO.of(f, language));
	}

	@Transactional(readOnly = true)
	public Page<FoodSupplementDTO> findAllByApplicantIdentifier(final String applicantIdentifier, Language language, final Pageable pageable) {
		Page<FoodSupplement> p = repository.findAllByApplicant_IdentifierAndI18ns_foodSupplementI18nIdentity_languageId(applicantIdentifier, language.getLanguageId(), pageable);
		return p.map(f -> FoodSupplementDTO.of(f, language));
	}

	@Transactional
	public FoodSupplement create(final FoodSupplement foodSupplement) {
		if (foodSupplement == null) {
			throw new RuntimeException("Food supplement is required!");
		}
		// TODO: To remove if is not necessary - save from s3125 service
//		foodSupplement.setOperator(foodSupplement.getOperator() != null
//				? contractorService.findById(foodSupplement.getOperator().getId()) : null);
//		foodSupplement.setOperatorType(foodSupplement.getOperatorType() != null
//				? nomenclatureService.findByCode(foodSupplement.getOperatorType().getCode()) : null);
//
//		if (foodSupplement.getFoodSupplementType() != null) {
//			foodSupplement.setFoodSupplementType(nomenclatureService.findByCode(foodSupplement.getFoodSupplementType().getCode()));
//		}
//		if (foodSupplement.getMeasuringUnit() != null) {
//			foodSupplement.setMeasuringUnit(nomenclatureService.findByCode(foodSupplement.getMeasuringUnit().getCode()));
//		}
//		if (!CollectionUtils.isEmpty(foodSupplement.getCountries())) {
//			List<Country> countries = new ArrayList<>();
//			foodSupplement.getCountries().forEach(c -> countries.add(countryService.findByCode(c.getCode())));
//			foodSupplement.getCountries().clear();
//			foodSupplement.getCountries().addAll(countries);
//		}

        return repository.save(foodSupplement);
	}

	@Transactional
	public FoodSupplement update(final Long id, final FoodSupplementDTO dto, Language language) {
		if (id == null || id <= 0) {
			throw new RuntimeException("ID field is required");
		}
		if (!id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}

		FoodSupplement current = findById(id);
		current.setEnabled(dto.getEnabled());
		current.setApplicant(dto.getApplicantId() != null && dto.getApplicantId() > 0
				? contractorService.findById(dto.getApplicantId())
				: null);
//		current.setApplicantType(dto.getApplicantTypeCode() != null
//				? nomenclatureService.findByCode(dto.getApplicantTypeCode())
//				: null);

		FoodSupplementI18n i18n = current.getI18n(language);
		i18n.setName(dto.getName());
		i18n.setPurpose(dto.getPurpose());
		i18n.setIngredients(dto.getIngredients());
		i18n.setDescription(dto.getDescription());

		if (!CollectionUtils.isEmpty(dto.getCountries())) {
			List<Country> countries = new ArrayList<>();
			dto.getCountries().forEach(c -> countries.add(countryService.findByCode(c.getCode())));
			current.getCountries().clear();
			current.getCountries().addAll(countries);
		}
		if (StringUtils.hasText(dto.getMeasuringUnitCode())) {
			current.setMeasuringUnit(nomenclatureService.findByCode(dto.getMeasuringUnitCode()));
		}

		return repository.save(current);
	}

	@Transactional(readOnly = true)
    public Page<FoodSupplement> search(final String param, final Language language, final Pageable pageable) {
		return repository.search(param, language.getLanguageId(), pageable);
    }

	@Transactional(readOnly = true)
	public List<FoodSupplementDTO> findRevisions(final Long id, Language language) {
		List<FoodSupplementDTO> revisions = new ArrayList<>();
		repository.findRevisions(id).get().forEach(r -> {
			FoodSupplementDTO rev = FoodSupplementDTO.of(r.getEntity(), language);
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
