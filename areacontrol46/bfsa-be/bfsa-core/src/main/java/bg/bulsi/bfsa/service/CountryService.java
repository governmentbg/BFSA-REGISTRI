package bg.bulsi.bfsa.service;

import bg.bulsi.bfsa.dto.CountryDTO;
import bg.bulsi.bfsa.exception.EntityNotFoundException;
import bg.bulsi.bfsa.model.Country;
import bg.bulsi.bfsa.model.CountryI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.repository.CountryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class CountryService {
    private final CountryRepository repository;
    private final static String COUNTRY_REQUIRED_ERROR = "Country is required!";

    public Page<Country> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Country> findAllEuMembers(){
        return repository.findAllByEuropeanUnionMemberIsTrue();
    }

    public Country findByCode(final String code) {
        return repository.findByCode(code).orElseThrow(() -> new EntityNotFoundException(Country.class, code));
    }

    public Country create(final Country country) {
        if (country == null) {
            throw new RuntimeException(COUNTRY_REQUIRED_ERROR);
        }
        return repository.save(country);
    }

    @Transactional(readOnly = true)
    public List<Country> search(final String param, final Language language, final Pageable pageable) {
        return repository.search(param, language.getLanguageId(), pageable);
    }

    @Transactional
    public Country update(String code, CountryDTO dto, Language language) {
        if (!StringUtils.hasText(code)) {
            throw new RuntimeException("Code field is required.");
        }
        if (!code.equals(dto.getCode())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter code.");
        }

        Country current = findByCode(code);
        current.setCode(dto.getCode());
        current.setIsoAlpha3(dto.getIsoAlpha3());
        current.setContinent(dto.getContinent());
        current.setCurrencyCode(dto.getCurrencyCode());
        current.setEnabled(dto.getEnabled());

        CountryI18n i18n = current.getI18n(language);
        i18n.setName(dto.getName());
        i18n.setCapital(dto.getCapital());
        i18n.setContinentName(dto.getContinentName());
        i18n.setDescription(dto.getDescription());

        return repository.save(current);
    }
}
