package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.ClassifierDTO;
import bg.bulsi.bfsa.dto.ClassifierVO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.service.ClassifierService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequestMapping("/classifiers")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ClassifierController {

	private final ClassifierService service;
	private final LanguageService languageService;

	@GetMapping
	public ResponseEntity<?> findAllParentsClassifierVO(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		List<ClassifierVO> classifiers = service.findAllParentsClassifierVO(language);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(classifiers)
						.totalCount(classifiers.size()).build()
		);
	}

	@GetMapping(path = "/")
	public ResponseEntity<?> findAllParents(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		List<ClassifierDTO> classifierDTOS = service.findAllParents(language);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(classifierDTOS)
						.totalCount(classifierDTOS.size()).build()
		);
	}

	@GetMapping(path = "/{code}")
	public ResponseEntity<?> findByCode(@PathVariable final String code,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		ClassifierDTO dto = service.findByCodeDTO(code, language);
		return ResponseEntity.ok().body(dto);
	}

	@GetMapping(path = "/{code}/sub-classifiers")
	public ResponseEntity<?> findAllByParentCodeVO(@PathVariable final String code,
												   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language lang = LocaleUtil.getLanguage(locale, languageService);
		List<ClassifierVO> list = service.findAllByParentCodeVO(code, lang);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(list)
						.totalCount(list.size()).build()
		);
	}

	@PostMapping(path = "/create-next")
	public ResponseEntity<?> createNext(@RequestBody final ClassifierDTO dto,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(ClassifierDTO.of(service.createNext(ClassifierDTO.to(dto, language)), language));
	}

	@PostMapping(path = "/{code}/create-next")
	public ResponseEntity<?> createNext(@PathVariable @NotNull final String code,
										@RequestBody @Valid final ClassifierDTO dto,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(ClassifierDTO.of(service.createNext(code, ClassifierDTO.to(dto, language)), language));
	}

	@PostMapping(path = "/{code}/add-next")
	public ResponseEntity<?> addNext(@PathVariable @NotNull final String code,
									 @RequestBody @Valid final ClassifierDTO dto,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {

		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(ClassifierDTO.of(service.addNext(code, dto, language), language));
	}

	@PutMapping(path = "/{code}")
	public ResponseEntity<?> update(@PathVariable @NotNull final String code,
									@RequestBody @Valid final ClassifierDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.update(code, dto, language));
	}
}
