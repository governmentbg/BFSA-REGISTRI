package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.NomenclatureDTO;
import bg.bulsi.bfsa.dto.NomenclatureVO;
import bg.bulsi.bfsa.exception.EntityAlreadyExistException;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import bg.bulsi.bfsa.model.NomenclatureI18n;
import bg.bulsi.bfsa.repository.NomenclatureRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class NomenclatureService {

	private final NomenclatureRepository repository;

//	@Transactional(readOnly = true)
	public Nomenclature findByCode(final String code) {
		return repository.findByCode(code).orElseThrow(() -> new EntityNotFoundException(Nomenclature.class, code));
	}

	@Transactional(readOnly = true)
	public NomenclatureDTO findByCodeDTO(final String code, final Language language) {
		return NomenclatureDTO.of(repository.findByCode(code)
				.orElseThrow(() -> new EntityNotFoundException(Nomenclature.class, code)), language);
	}

	@Transactional(readOnly = true)
	public Nomenclature findByExternalCode(final String externalCode) {
		return repository.findByExternalCode(externalCode)
				.orElseThrow(() -> new EntityNotFoundException(Nomenclature.class, externalCode));
	}

	@Transactional(readOnly = true)
	public NomenclatureDTO findByExternalCodeDTO(final String externalCode, final Language language){
		return NomenclatureDTO.of(repository.findByExternalCode(externalCode)
				.orElseThrow(() -> new EntityNotFoundException(Nomenclature.class, externalCode)), language);
	}

	@Transactional(readOnly = true)
	public List<NomenclatureVO> findAllByParentCodeVO(final String parentCode, final Language language) {
		return repository.findAllByParentCodeVO(parentCode, language.getLanguageId());
	}

	public Nomenclature findByCodeOrNull(final String code) {
		return repository.findByCode(code).orElse(null);
	}

	@Transactional(readOnly = true)
	public List<NomenclatureDTO> findAllParents(Language language) {
		return NomenclatureDTO.of(repository.findAllByParentIsNull(), language);
	}

	@Transactional(readOnly = true)
	public List<NomenclatureVO> findAllParentsNomenclatureVO(Language language) {
		return repository.findAllByParentNomenclatureVO(language.getLanguageId());
	}

	private String findLastCode(final String parentCode) {
		return repository.findLastCode(parentCode);
	}

	private String findLastParentCode() {
		return repository.findLastParentCode();
	}

	@Transactional
	public Nomenclature createNext(final Nomenclature nom) {
		if (nom == null) {
			throw new RuntimeException("Nomenclature is required");
		}
		if (StringUtils.hasText(nom.getCode()) && repository.existsById(nom.getCode())) {
			throw new EntityAlreadyExistException(Nomenclature.class, nom.getCode());
		}

		nom.setCode(calcCode());
		nom.setEnabled(true);

		if (!CollectionUtils.isEmpty(nom.getI18ns())) {
			nom.getI18ns().forEach(i18n -> i18n.getNomenclatureI18nIdentity().setCode(nom.getCode()));
		}

		if (!CollectionUtils.isEmpty(nom.getSubNomenclatures())) {
			if (nom.getSubNomenclatures().size() > 99) {
				throw new RuntimeException("Code for sub nomenclature exceeded!");
			}

			String lastCodeStr = nom.getCode();
			long lastCode = Long.parseLong(lastCodeStr);
			long nextCode = lastCode;
			for (Nomenclature sub: nom.getSubNomenclatures()) {
				sub.setCode(lastCodeStr.replaceFirst(Long.toString(lastCode), Long.toString(++nextCode)));
				sub.setParent(nom);
				sub.setEnabled(true);
			}
		}

		return repository.save(nom);
	}

	@Transactional
	public Nomenclature createNext(final String code, final Nomenclature nom) {
		if (nom == null) {
			throw new RuntimeException("Nomenclature is required");
		}
		Nomenclature parent = findByCode(code);
		if (parent.getParent() != null) {
			throw new RuntimeException("Nomenclature has its own parent");
		}
		nom.setParent(parent);
		nom.setCode(calcCode(code));
		nom.setEnabled(true);

		return repository.save(nom);
	}

	@Transactional
	public Nomenclature addNext(final String code, final NomenclatureDTO dto, final Language language) {
		if (!StringUtils.hasText(code) || !code.equals(dto.getCode())) {
			throw new RuntimeException("Nomenclature code is not equal to dto code.");
		}
		Nomenclature nom = findByCode(code);
		nom.getI18ns().add(new NomenclatureI18n(dto.getName(), dto.getDescription(), nom, language));
		nom.setEnabled(true);

		return repository.save(nom);
	}

	@Transactional
	public NomenclatureDTO update(final String code, final NomenclatureDTO dto, Language language) {
		Nomenclature n = findByCode(code);
		n.setSymbol(dto.getSymbol());
		NomenclatureI18n i18n = n.getI18n(language);
		i18n.setName(dto.getName());
		i18n.setDescription(dto.getDescription());
		if (dto.getEnabled() != null) {
			n.setEnabled(dto.getEnabled());
		}

		return NomenclatureDTO.of(repository.save(n), language);
	}

	private String calcCode() {
		return calcCode(null);
	}

	private String calcCode(final String parentCode) {
		String lastCodeStr = StringUtils.hasText(parentCode)
				? findLastCode(parentCode)
				: findLastParentCode();

		if (!StringUtils.hasText(lastCodeStr)) {
			if (!StringUtils.hasText(parentCode)) {
				return "00100";
			}
			lastCodeStr = parentCode;
		}

		long lastCode = Long.parseLong(lastCodeStr);
		long nextCode = lastCode + (StringUtils.hasText(parentCode) ? 1 : 100);

		if (nextCode > 99900) {
			throw new RuntimeException("Code for parent nomenclature exceeded!");
		}

		String nextCodeStr = Long.toString(nextCode);
		return "00000".substring(nextCodeStr.length()) + nextCodeStr;
	}
}
