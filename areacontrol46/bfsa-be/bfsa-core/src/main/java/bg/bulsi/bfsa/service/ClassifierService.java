package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.ClassifierDTO;
import bg.bulsi.bfsa.dto.ClassifierVO;
import bg.bulsi.bfsa.exception.EntityAlreadyExistException;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.ClassifierI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.ClassifierRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ClassifierService {

	private final ClassifierRepository repository;

	public Classifier findByCode(final String code) {
		return repository.findByCode(code).orElseThrow(() -> new EntityNotFoundException(Classifier.class, code));
	}

	@Transactional(readOnly = true)
	public ClassifierDTO findByCodeDTO(final String code, Language language) {
		return ClassifierDTO.of(repository.findByCode(code)
				.orElseThrow(() -> new EntityNotFoundException(Classifier.class, code)), language);
	}

	public Classifier findByExternalCode(final String externalCode) {
		return repository.findByExternalCode(externalCode)
				.orElseThrow(() -> new EntityNotFoundException(Classifier.class, externalCode));
	}

	public Classifier findByExternalCodeOrNull(final String externalCode) {
		return repository.findByExternalCode(externalCode).orElse(null);
	}

	@Transactional(readOnly = true)
	public List<ClassifierVO> findAllByParentCodeVO(final String parentCode, final Language language) {
		return repository.findAllByParentCodeVO(parentCode, language.getLanguageId());
	}

	public Classifier findByCodeOrNull(final String code) {
		return repository.findByCode(code).orElse(null);
	}

	@Transactional(readOnly = true)
	public List<ClassifierVO> findAllParentsClassifierVO(Language language) {
		return repository.findAllByParentsClassifierVO(language.getLanguageId());
	}

	@Transactional(readOnly = true)
	public List<ClassifierDTO> findAllParents(Language language) {
		return ClassifierDTO.of(repository.findAllByParentIsNull(), language);
	}

	private String findLastCode(final String parentCode) {
		return repository.findLastCode(parentCode);
	}

	private String findLastParentCode() {
		return repository.findLastParentCode();
	}

	@Transactional
	public Classifier createNext(final Classifier classifier) {
		if (classifier == null) {
			throw new RuntimeException("Classifier is required");
		}
		if (StringUtils.hasText(classifier.getCode()) && repository.existsById(classifier.getCode())) {
			throw new EntityAlreadyExistException(Classifier.class, classifier.getCode());
		}

		classifier.setCode(calcCode());
		classifier.setEnabled(true);

		if (!CollectionUtils.isEmpty(classifier.getI18ns())) {
			classifier.getI18ns().forEach(i18n -> i18n.getClassifierI18nIdentity().setCode(classifier.getCode()));
		}

		if (!CollectionUtils.isEmpty(classifier.getSubClassifiers())) {
			if (classifier.getSubClassifiers().size() > 999) {
				throw new RuntimeException("Code for sub classifier exceeded!");
			}

			String lastCodeStr = classifier.getCode();
			long lastCode = Long.parseLong(lastCodeStr);
			long nextCode = lastCode;
			for (Classifier sub: classifier.getSubClassifiers()) {
				sub.setCode(lastCodeStr.replaceFirst(Long.toString(lastCode), Long.toString(++nextCode)));
				sub.setParent(classifier);
				sub.setEnabled(true);
			}
		}

		return repository.save(classifier);
	}

	@Transactional
	public Classifier createNext(final String code, final Classifier classifier) {
		if (classifier == null) {
			throw new RuntimeException("Classifier is required");
		}
		Classifier parent = findByCode(code);
//		if (parent.getParent() != null) {
//			throw new RuntimeException("Classifier has its own parent");
//		}
		classifier.setParent(parent);
		classifier.setCode(calcCode(code));
		classifier.setEnabled(true);

		return repository.save(classifier);
	}

	@Transactional
	public Classifier addNext(final String code, final ClassifierDTO dto, final Language language) {
		if (!StringUtils.hasText(code) || !code.equals(dto.getCode())) {
			throw new RuntimeException("Classifier code is not equal to dto code.");
		}
		Classifier classifier = findByCode(code);
		classifier.getI18ns().add(new ClassifierI18n(dto.getName(), dto.getDescription(), classifier, language));
		classifier.setEnabled(true);

		return repository.save(classifier);
	}

	@Transactional
	public ClassifierDTO update(final String code, final ClassifierDTO dto, Language language) {
		Classifier classifier = findByCode(code);
		classifier.setSymbol(dto.getSymbol());
		ClassifierI18n i18n = classifier.getI18n(language);
		i18n.setName(dto.getName());
		i18n.setDescription(dto.getDescription());
		if (dto.getEnabled() != null) {
			classifier.setEnabled(dto.getEnabled());
		}

		Classifier c = repository.save(classifier);

		return ClassifierDTO.of(c, language);
	}

	private String calcCode() {
		return calcCode(null);
	}

	private String calcCode(final String parentCode) {
		String lastCodeStr = StringUtils.hasText(parentCode)
				? findLastCode(parentCode.substring(0 , 4))
				: findLastParentCode();

		if (!StringUtils.hasText(lastCodeStr)) {
			if (!StringUtils.hasText(parentCode)) {
				return "0001000";
			}
			lastCodeStr = parentCode;
		}

		long lastCode = Long.parseLong(lastCodeStr);
		long nextCode = lastCode + (StringUtils.hasText(parentCode) ? 1 : 1000);

		if (nextCode > 9999000) {
			throw new RuntimeException("Code for parent nomenclature exceeded!");
		}

		String nextCodeStr = Long.toString(nextCode);
		return "0000000".substring(nextCodeStr.length()) + nextCodeStr;
	}
}
