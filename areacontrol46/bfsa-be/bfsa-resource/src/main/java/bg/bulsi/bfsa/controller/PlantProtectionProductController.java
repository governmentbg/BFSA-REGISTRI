package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.PlantProtectionProductDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.PlantProtectionProductService;
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
@RequestMapping("/plant-protection-products")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class PlantProtectionProductController {

	private final PlantProtectionProductService service;
	private final LanguageService languageService;


	@GetMapping("/")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale, final Pageable pageable) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findAll(pageable).map(f -> PlantProtectionProductDTO.of(f, language)));
	}


	@GetMapping("/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> getById(
			@PathVariable final Long id,
			@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		return ResponseEntity.ok(PlantProtectionProductDTO.of(service.findById(id), LocaleUtil.getLanguage(locale, languageService)));
	}


	@GetMapping(params = { "q" })
	@Secured({ RolesConstants.ROLE_ADMIN })
	public ResponseEntity<?> search(
			@RequestParam(name = "q", required = false) final String param,
			@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
			final Pageable pageable) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.search(param, language, pageable).map(f -> PlantProtectionProductDTO.of(f, language)));
	}


	@PostMapping("/create")
	@Secured({ RolesConstants.ROLE_ADMIN })
	public ResponseEntity<?> create(
			@RequestBody final PlantProtectionProductDTO dto,
			@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(PlantProtectionProductDTO.of(service.create(PlantProtectionProductDTO.to(dto, language)), language));
	}


	@PutMapping("/{id}")
	@Secured({ RolesConstants.ROLE_ADMIN })
	public ResponseEntity<?> update(
			@PathVariable @NotNull final Long id,
			@RequestBody final PlantProtectionProductDTO dto,
			@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		if (dto.getId() != null && !id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(PlantProtectionProductDTO.of(service.update(id, PlantProtectionProductDTO.to(dto, language), language), language));
	}


	@GetMapping(path = "/{id}/history")
	@Secured({ RolesConstants.ROLE_ADMIN })
	public ResponseEntity<?> history(
			@PathVariable final Long id,
			@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findRevisions(id, language));
	}


//	@GetMapping("/contractor-id/{contractorId}")
//	@Secured({ Role.ROLE_ADMIN })
//	public ResponseEntity<?> getByContractorId(
//			@PathVariable final Long contractorId,
//			@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//		Language language = LocaleUtil.getLanguage(locale, languageService);
//		return ResponseEntity.ok(PlantProtectionProductDTO.of(service.findByContractorId(contractorId), language));
//	}
}
