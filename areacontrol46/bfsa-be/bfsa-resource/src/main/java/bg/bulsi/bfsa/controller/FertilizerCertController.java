package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.FertilizerCertDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.FertilizerCertService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/fertilizer-certs")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FertilizerCertController {

	private final FertilizerCertService service;
	private final LanguageService languageService;

	@GetMapping
	public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
									 final Pageable pageable) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findAll(pageable)
				.map(f -> FertilizerCertDTO.of(f, language)));
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<?> findById(@PathVariable final Long id,
									  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		return ResponseEntity.ok().body(FertilizerCertDTO
				.of(service.findById(id), LocaleUtil.getLanguage(locale, languageService)));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@PostMapping(path = "/create")
	public ResponseEntity<?> create(@RequestBody final FertilizerCertDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(FertilizerCertDTO
				.of(service.create(FertilizerCertDTO.to(dto, language)), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@PutMapping(path = "/{id}")
	public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
									@RequestBody @Valid final FertilizerCertDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		if (id == null || id <= 0) {
			throw new RuntimeException("Requested parameters missing!");
		}

		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.update(id, dto, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@GetMapping(path = "/{id}/history")
	public ResponseEntity<?> history(@PathVariable Long id,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findRevisions(id, language));
	}
}