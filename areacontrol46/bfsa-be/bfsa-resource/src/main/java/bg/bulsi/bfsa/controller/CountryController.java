package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.CountryDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.CountryService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/countries")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class CountryController {
    private final CountryService service;
    private final LanguageService languageService;

    @GetMapping("/")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale, final Pageable pageable) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findAll(pageable).map(p -> CountryDTO.of(p, language)));
    }

    @GetMapping("/eu-members")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> findAllEuMembers(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findAllEuMembers().stream().map(p -> CountryDTO.of(p, language)));
    }

    @GetMapping("/{code}")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> findByCode(@PathVariable final String code,
                                        @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        return ResponseEntity.ok(
                CountryDTO.of(service.findByCode(code),
                        LocaleUtil.getLanguage(locale, languageService))
        );
    }

    @PostMapping("/create")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> create(@RequestBody final CountryDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(CountryDTO.of(service.create(CountryDTO.to(dto, language)), language));
    }

    @PutMapping("/{code}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> update(@PathVariable @NotNull final String code,
                                    @RequestBody final CountryDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        if (!StringUtils.hasText(dto.getCode()) && !code.equals(dto.getCode())) {
            throw new RuntimeException("Path variable code does not match RequestBody parameter code.");
        }
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.update(code, dto, language));
    }

    @GetMapping(params = {"q"})
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> search(@RequestParam(name = "q", required = false) final String param,
                                    @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
                                    final Pageable pageable) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.search(param, language, pageable)
                .stream().map(f -> CountryDTO.of(f, language)));
    }
}
