package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.AddressService;
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
@RequestMapping("/addresses")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class AddressController {

	private final AddressService service;
	private final LanguageService languageService;

	@GetMapping("/")
	public ResponseEntity<?> findAll(final Pageable pageable,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findAll(pageable).map(a -> AddressDTO.of(a, language)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable final Long id,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(AddressDTO.of(service.findById(id), language));
	}

	@GetMapping(path = "/{id}/address-info")
	public ResponseEntity<?> getAddressInfo(@PathVariable @NotNull final Long id,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.getAddressInfo(id, language.getLanguageId()));
	}

	@GetMapping(params = {"q"})
	public ResponseEntity<?> search(@RequestParam(name = "q", required = false) final String param,
									final Pageable pageable,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.search(param, pageable).map(a -> AddressDTO.of(a, language)));
	}

	@PostMapping("/create")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> create(@RequestBody final AddressDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(AddressDTO.of(service.create(AddressDTO.to(dto)), language));
	}

	@PutMapping("/{id}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
									@RequestBody final AddressDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		if (dto.getId() != null && !id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		return ResponseEntity.ok().body(AddressDTO.of(service.update(id, dto), language));
	}

	@GetMapping(path = "/{id}/history")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> history(@PathVariable final Long id,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findRevisions(id, language));
	}
}
