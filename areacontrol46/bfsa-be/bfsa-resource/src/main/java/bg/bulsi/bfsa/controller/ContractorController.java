package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.ContractorDTO;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.LogoutRequest;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.ContractorService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.RefreshTokenService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/contractors")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ContractorController {
	private final ContractorService service;
	private final RefreshTokenService refreshTokenService;
	private final LanguageService languageService;

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/")
	public Page<ContractorDTO> findAll(final Pageable pageable,
									   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return service.findAllDTO(pageable, language);
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/me")
	public ResponseEntity<?> getCurrentUser(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		UserDetails currentUser = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(ContractorDTO.of(service.findByUsername(currentUser.getUsername()), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/username/{username}")
	public ResponseEntity<?> findByUsername(@PathVariable final String username,
											@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(ContractorDTO.of(service.findByUsername(username), language));
	}

	@GetMapping("/{id}")
	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	public ResponseEntity<?> findById(@PathVariable final Long id,
									  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findByIdDTO(id, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/{id}/records")
	public ResponseEntity<?> records(@PathVariable final Long id) {
		List<RecordDTO> records = service.records(id);
		return ResponseEntity.ok()
				.body(ApiListResponse.builder()
						.results(records)
						.totalCount(records.size())
						.build());
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping(path = "/{id}/contractor-facilities")
	public ResponseEntity<?> findAllFacilities(@PathVariable final Long id,
											   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.findAllContractorFacilities(id, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping(params = {"q"})
	public Page<ContractorDTO> search(@RequestParam(name = "q", required = false) final String param,
									  final Pageable pageable,
									  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);

		return service.search(param, pageable, language);
	}

	@PostMapping("/signout")
	public ResponseEntity<?> logout(@RequestBody final LogoutRequest request) {
		refreshTokenService.deleteByUserId(request.getId(), 1);
		return ResponseEntity.ok().body("User logged out");
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@PutMapping("/{id}/roles")
	public ResponseEntity<?> roles(@PathVariable @NotNull final Long id,
								   @RequestBody @NotEmpty final List<String> roles,
								   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(ContractorDTO.of(service.updateRoles(id, roles), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
									@RequestBody final ContractorDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		if (dto.getId() != null && !id.equals(dto.getId())) {
			throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
		}
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.update(id, ContractorDTO.to(dto), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@PutMapping("/{id}/{enabled}")
	public ResponseEntity<?> setEnabled(@PathVariable @NotNull final Long id,
										@PathVariable @NotNull final Boolean enabled,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(ContractorDTO.of(service.setEnabled(id, enabled), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping(path = "/{id}/history")
	public ResponseEntity<?> history(@PathVariable final Long id,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findRevisions(id, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping(path = "/{id}/facility-history")
	public ResponseEntity<?> facilityHistory(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
											 @PathVariable final Long id) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findRevisionsFacility(id, language));
	}

//	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//	@PostMapping("/{id}/facility")
//	public ResponseEntity<?> createFacility(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
//											@PathVariable @NotNull final String id,
//											@RequestBody final FacilityDTO dto) {
//		if (!StringUtils.hasText(id) || !id.equals(dto.getContractorId())) {
//			throw new RuntimeException("Requested parameters missing!");
//		}
//		Language language = LocaleUtil.getLanguage(locale, languageService);
//		return ResponseEntity.ok().body(service.createFacility(id, FacilityDTO.to(dto, language), language));
//	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@PutMapping("/{id}/facility")
	public ResponseEntity<?> updateFacility(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
											@PathVariable @NotNull final Long id,
											@RequestBody final FacilityDTO dto) {
		if (id == null || id <= 0 || dto.getId() == null || dto.getId() <= 0) {
			throw new RuntimeException("Requested parameters missing!");
		}
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.updateFacility(id, dto, language));
	}

//	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//	@PutMapping(path = "/{id}/facilities-owner/{newFacilitiesOwnerId}", params = {"comment"})
//	public ResponseEntity<?> changeFacilitiesOwner(@PathVariable @NotNull final String id,
//												   @PathVariable @NotNull final String newFacilitiesOwnerId,
//												   @RequestParam(name = "comment") final String comment,
//												   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//		if (!StringUtils.hasText(id) || !StringUtils.hasText(newFacilitiesOwnerId)) {
//			throw new RuntimeException("Requested parameters missing!");
//		}
//		if (id.equals(newFacilitiesOwnerId)) {
//			throw new RuntimeException("Path variable id is equal to newFacilitiesOwnerId!");
//		}
//		Language language = LocaleUtil.getLanguage(locale, languageService);
//		return ResponseEntity.ok().body(service.changeFacilitiesOwner(id, newFacilitiesOwnerId, comment, language));
//	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/register-code/{registerCode}")
	public ResponseEntity<?> findByRegisterCode(@PathVariable final String registerCode,
												@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findByRegisterCode(registerCode, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/register-code-and-branch/{registerCode}")
	public ResponseEntity<?> findByRegisterCodeAndBranch(@PathVariable final String registerCode,
														   final Pageable pageable,
														 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		 UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (userDetails == null || !userDetails.isEnabled() || !StringUtils.hasText(userDetails.getUsername())) {
				throw new RuntimeException("Login first");
			}
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service
				.findByRegisterCodeAndBranchId(userDetails.getUsername(), registerCode, pageable, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/division-code-and-branch/{divisionCode}")
	public ResponseEntity<?> findByDivisionCodeAndBranchId(@PathVariable final String divisionCode,
														 final Pageable pageable,
														   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (userDetails == null || !userDetails.isEnabled() || !StringUtils.hasText(userDetails.getUsername())) {
			throw new RuntimeException("Login first");
		}
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service
				.findByDivisionCodeAndBranchId(userDetails.getUsername(), divisionCode, pageable, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/{id}/vehicles")
	public ResponseEntity<?> findAllVehicles(@PathVariable final Long id,
												   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findAllVehicles(id, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/contractor-paper-service-type/{serviceType}")
	public ResponseEntity<?> findAllByContractorPaperServiceType(@PathVariable final String serviceType,
															 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		var results = service.findAllByContractorPaperServiceType(serviceType, language);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(results)
						.totalCount(results.size())
						.build()
		);
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/{id}/distance-trading")
	public ResponseEntity<?> findDistanceTradingByContractorId(@PathVariable final Long id,
																 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
        List<AddressDTO> results = service.findDistanceTradingByContractorId(id, language);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(results)
						.totalCount(results.size())
						.build()
		);
	}
}
