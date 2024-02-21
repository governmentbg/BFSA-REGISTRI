package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ActivityGroupDTO;
import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.BaseVO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.ActivityGroupService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/activity-groups")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ActivityGroupController {

	private final ActivityGroupService service;
	private final LanguageService languageService;

	@GetMapping
	public ResponseEntity<?> findAllParents(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Set<ActivityGroupDTO> parents = service.findAllParents(LocaleUtil.getLanguage(locale, languageService));
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(parents.stream().toList())
						.totalCount(parents.size()).build()
		);
	}

	@GetMapping("/parents")
	public ResponseEntity<?> findAllParentsVO(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		List<BaseVO> list = service.findAllParentsVO(language);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(list)
						.totalCount(list.size()).build()
		);
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<?> findById(@PathVariable final Long id,
									  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.getById(id, language));
	}

	@GetMapping(path = "/{id}/sub-activity-groups")
	public ResponseEntity<?> findAllByParentIdVO(@PathVariable final Long id,
									  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language lang = LocaleUtil.getLanguage(locale, languageService);
		List<BaseVO> list = service.findAllByParentIdVO(id, lang);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(list)
						.totalCount(list.size()).build()
		);
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@PostMapping(path = "/create")
	public ResponseEntity<?> create(@RequestBody final ActivityGroupDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(ActivityGroupDTO.of(service.create(ActivityGroupDTO.to(dto, language)), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@PostMapping(path = "/{id}/create")
	public ResponseEntity<?> create(@PathVariable final Long id, @RequestBody final ActivityGroupDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(ActivityGroupDTO.of(service.create(id, ActivityGroupDTO.to(dto, language)), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@PutMapping(path = "/{id}")
	public ResponseEntity<?> update(@PathVariable @NotNull final Long id, @RequestBody @Valid final ActivityGroupDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.update(id, ActivityGroupDTO.to(dto, language), language));
	}

	@Secured({RolesConstants.ROLE_ADMIN})
	@GetMapping(path = "/{id}/history")
	public ResponseEntity<?> history(@PathVariable Long id,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findRevisions(id, language));
	}
}