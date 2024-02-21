package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.MessageResourceDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.MessageResource;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.MessageResourceService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/message-resources")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class MessageResourceController {

	private final MessageResourceService service;
	private final LanguageService languageService;

	@GetMapping
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale, final Pageable pageable) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		List<MessageResource> list = service.findAll(language.getLanguageId(), pageable);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(MessageResourceDTO.of(list))
						.totalCount(list.size()).build()
		);
	}

	@GetMapping(path = "/")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> findAll() {
		List<MessageResource> list = service.findAll();
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(MessageResourceDTO.of(list))
						.totalCount(list.size()).build()
		);
	}

	@GetMapping(path = "/{code}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> findByCode(@PathVariable @NotNull final String code,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		return ResponseEntity.ok().body(MessageResourceDTO.of(
			service.findById(new MessageResource.MessageResourceIdentity(code, LocaleUtil.getLanguage(locale, languageService).getLanguageId()))
		));
	}

	@GetMapping(path = "/{code}/")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> findAllByCode(@PathVariable @NotNull final String code) {

		return ResponseEntity.ok().body(MessageResourceDTO.of(
				service.findAllByCode(code), languageService.findAll()
		));
	}

	@PutMapping(path = "/{code}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> update(@PathVariable @NotNull final String code,
									@RequestBody @Valid final MessageResourceDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		return ResponseEntity.ok().body(MessageResourceDTO.of(
				service.update(new MessageResource.MessageResourceIdentity(code, LocaleUtil.getLanguage(locale, languageService).getLanguageId()), dto)
		));
	}

	@PutMapping
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> update(@RequestBody @NotNull @Valid final List<MessageResourceDTO> dtos) {
		List<MessageResourceDTO> result = new ArrayList<>();
		dtos.forEach(d -> result.add(
				MessageResourceDTO.of(service.updateOrCreate(MessageResource.MessageResourceIdentity.builder().code(d.getCode()).languageId(d.getLanguageId()).build(), d)))
		);
		return ResponseEntity.ok().body(result);
	}

}
