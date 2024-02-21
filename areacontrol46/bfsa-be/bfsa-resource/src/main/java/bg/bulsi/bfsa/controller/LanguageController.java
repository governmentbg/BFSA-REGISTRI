package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.LanguageDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.LanguageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/langs")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LanguageController {

	private final LanguageService service;

	@GetMapping(path = "/")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> findAll() {
		List<Language> languages = service.findAll();
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(LanguageDTO.of(languages))
						.totalCount(languages.size()).build()
		);
	}

	@GetMapping(path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> findById(@PathVariable @NotNull final String id) {
		return ResponseEntity.ok().body(LanguageDTO.of(service.findById(id)));
	}

	@PostMapping(path = "/")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> create(@RequestBody @Valid final LanguageDTO dto) {
		return ResponseEntity.ok().body(LanguageDTO.of(service.create(LanguageDTO.to(dto))));
	}

	@PutMapping(path = "/{id}")
	@Secured({RolesConstants.ROLE_ADMIN})
	public ResponseEntity<?> update(@PathVariable @NotNull final String id,
									@RequestBody @Valid final LanguageDTO dto) {
		return ResponseEntity.ok().body(LanguageDTO.of(service.update(id, dto)));
	}

}
