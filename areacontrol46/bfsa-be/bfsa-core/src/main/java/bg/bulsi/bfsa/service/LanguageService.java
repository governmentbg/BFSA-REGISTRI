package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.LanguageDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.repository.LanguageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LanguageService {

	private static final String GET_MAIN_LANGUAGE_CACHED = "getMainLanguage";

	private final LanguageRepository repository;

	public Language findById(final String languageId) {
		return !StringUtils.hasText(languageId)
				? repository.findAllByEnabledIsTrueAndMainIsTrue().orElseThrow(() -> new EntityNotFoundException(User.class, languageId))
				: repository.findById(languageId).orElseThrow(() -> new EntityNotFoundException(User.class, languageId));
	}

	public Language findByIdOrNull(final String languageId) {
		return repository.findById(languageId).orElse(null);
	}

	public List<Language> findAll() {
		return repository.findAll();
	}

	// Should be called from outside the class
	@Cacheable(GET_MAIN_LANGUAGE_CACHED)
	public Language getMainLanguage() {
		return findById(null);
	}

	@Transactional
	@CacheEvict(value = GET_MAIN_LANGUAGE_CACHED, allEntries = true)
	public Language create(final Language entity) {
		if (entity == null || !StringUtils.hasText(entity.getLanguageId())) {
			throw new RuntimeException("Language id is required");
		}

		if (repository.existsById(entity.getLanguageId())) {
			throw new RuntimeException("Language with id " + entity.getLanguageId() + " already exists");
		}

		if (Boolean.TRUE.equals(entity.getMain())) {
			removeMain();
		}
		return repository.save(entity);
	}

	@Transactional
	@CacheEvict(value = GET_MAIN_LANGUAGE_CACHED, allEntries = true)
	public Language update(final String languageId, final LanguageDTO dto) {
		if (!StringUtils.hasText(languageId) || dto == null || !languageId.equals(dto.getLanguageId())) {
			throw new RuntimeException("Language id is not equal to dto language id");
		}
		Language entity = findById(languageId);
		if (Boolean.TRUE.equals(entity.getMain())) {
			if (!dto.getMain() || !dto.getEnabled()) {
				throw new RuntimeException("Choose another language as main language before updating this one");
			}
			removeMain();
		} else if (Boolean.TRUE.equals(dto.getMain()) && dto.getEnabled()) {
			removeMain();
		}

		entity.setMain(dto.getMain());
		entity.setName(dto.getName());
		entity.setLocale(dto.getLocale());
		entity.setDescription(dto.getDescription());
		entity.setEnabled(dto.getEnabled());

		return repository.save(entity);
	}

	private void removeMain() {
		List<Language> mains = repository.findAllByMainIsTrue();
		if (!CollectionUtils.isEmpty(mains)) {
			mains.forEach(m -> m.setMain(false));
		}
		repository.saveAll(mains);
	}
}
