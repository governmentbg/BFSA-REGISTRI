package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.dto.VehicleDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.VehicleService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
@RequestMapping("/vehicles")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class VehicleController {

	private final VehicleService service;
	private final LanguageService languageService;

	@GetMapping("/")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale, final Pageable pageable) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findAll(pageable, language));
	}

	@GetMapping("/{id}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> getById(@PathVariable final Long id,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		return ResponseEntity.ok(service.getById(id, LocaleUtil.getLanguage(locale, languageService)));
	}

	@GetMapping(params = {"q"})
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> search(@RequestParam(name = "q", required = false) final String param,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
									final Pageable pageable) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.search(param, language, pageable).map(f -> VehicleDTO.of(f, language)));
	}

	@PostMapping("/create")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> create(@RequestBody final VehicleDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(VehicleDTO.of(service.create(VehicleDTO.to(dto, language)), language));
	}

	@PutMapping("/{id}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
									@RequestBody final VehicleDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		if (dto.getId() != null && !id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.update(id, VehicleDTO.to(dto, language), language));
	}

	@PostMapping("/{id}/inspection")
	@Secured({RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> createInspection(@PathVariable Long id,
											  @RequestBody @Valid final InspectionDTO dto,
											  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		if (dto.getVehicleId() == null || dto.getVehicleId() <= 0 || !id.equals(dto.getVehicleId())) {
			throw new RuntimeException("Vehicle ID is required.");
		}
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.createInspection(id, dto, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@PutMapping("/{id}/complete-inspection")
	public ResponseEntity<?> completeInspection(@PathVariable final Long id, @RequestBody final InspectionDTO dto,
												@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.completeInspection(id, dto, language));
	}


	@PutMapping("/{id}/update-status")
	@Secured({RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> updateStatus(@PathVariable @NotNull final Long id,
									@RequestBody final KeyValueDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		if (dto.getId() != null && !id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.updateStatus(id, dto, language));
	}
}
