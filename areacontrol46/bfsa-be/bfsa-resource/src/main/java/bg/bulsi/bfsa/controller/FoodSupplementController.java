package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.FoodSupplementDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.FoodSupplementService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

@Slf4j
@RestController
@RequestMapping("/food-supplements")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FoodSupplementController {

    private final FoodSupplementService service;
    private final LanguageService languageService;

    //	@Secured({RolesConstants.ROLE_ADMIN})
    @GetMapping("/")
    public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale, final Pageable pageable) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findAll(pageable).map(f -> FoodSupplementDTO.of(f, language)));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<?> findAllByApplicantId(@PathVariable final Long applicantId,
                                                  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale, final Pageable pageable) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findAllByApplicantId(applicantId, language, pageable));
    }

    @GetMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> getById(@PathVariable final Long id,
                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        return ResponseEntity.ok(FoodSupplementDTO.of(service.findById(id), LocaleUtil.getLanguage(locale, languageService)));
    }

    @GetMapping(params = {"q"})
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> search(@RequestParam(name = "q", required = false) final String param,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
                                    final Pageable pageable) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.search(param, language, pageable)
                .map(f -> FoodSupplementDTO.of(f, language)));
    }

    @PostMapping("/create")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> create(@RequestBody final FoodSupplementDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(FoodSupplementDTO.of(service.create(FoodSupplementDTO.to(dto, language)), language));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
                                    @RequestBody final FoodSupplementDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        if (dto.getId() != null && !id.equals(dto.getId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(FoodSupplementDTO.of(service.update(id, dto, language), language));
    }

    //	@Secured({RolesConstants.ROLE_ADMIN})
    @GetMapping(path = "/{id}/history")
    public ResponseEntity<?> history(@PathVariable final Long id,
                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findRevisions(id, language));
    }
}
