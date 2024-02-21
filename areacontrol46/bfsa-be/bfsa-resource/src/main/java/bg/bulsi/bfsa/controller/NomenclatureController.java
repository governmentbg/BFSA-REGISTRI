package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.NomenclatureDTO;
import bg.bulsi.bfsa.dto.NomenclatureVO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.NomenclatureService;
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
@RequestMapping("/noms")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class NomenclatureController {

	private final NomenclatureService service;
	private final LanguageService languageService;

	@GetMapping(path = "/{code}")
	public ResponseEntity<?> findByCode(@PathVariable final String code,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		NomenclatureDTO n = service.findByCodeDTO(code, language);
		return ResponseEntity.ok().body(n);
	}

	@GetMapping(path = "/{externalCode}/external-code")
	public ResponseEntity<?> findByExternalCode(@PathVariable final String externalCode,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		NomenclatureDTO n = service.findByExternalCodeDTO(externalCode, language);
		return ResponseEntity.ok().body(n);
	}

	@GetMapping(path = "/{code}/sub-nomenclatures")
	public ResponseEntity<?> findAllByParentCodeVO(@PathVariable final String code,
												 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language lang = LocaleUtil.getLanguage(locale, languageService);
		List<NomenclatureVO> list = service.findAllByParentCodeVO(code, lang);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(list)
						.totalCount(list.size()).build()
		);
	}

	@GetMapping(path = "/")
	public ResponseEntity<?> findAllParents(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		List<NomenclatureDTO> nomenclatures = service.findAllParents(language);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(nomenclatures)
						.totalCount(nomenclatures.size()).build()
		);
	}

	@GetMapping
	public ResponseEntity<?> findAllParentsNomenclatureVO(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		List<NomenclatureVO> nomenclatures = service.findAllParentsNomenclatureVO(language);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(nomenclatures)
						.totalCount(nomenclatures.size()).build()
		);
	}

	@PostMapping(path = "/create-next")
	public ResponseEntity<?> createNext(@RequestBody final NomenclatureDTO dto,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(NomenclatureDTO.of(service.createNext(NomenclatureDTO.to(dto, language)), language));
	}

	@PostMapping(path = "/{code}/create-next")
	public ResponseEntity<?> createNext(@PathVariable @NotNull final String code,
										@RequestBody @Valid final NomenclatureDTO dto,
										@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(NomenclatureDTO.of(service.createNext(code, NomenclatureDTO.to(dto, language)), language));
	}

	@PostMapping(path = "/{code}/add-next")
	public ResponseEntity<?> addNext(@PathVariable @NotNull final String code,
									 @RequestBody @Valid final NomenclatureDTO dto,
									 @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {

		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(NomenclatureDTO.of(service.addNext(code, dto, language), language));
	}

	@PutMapping(path = "/{code}")
	public ResponseEntity<?> update(@PathVariable @NotNull final String code,
									@RequestBody @Valid final NomenclatureDTO dto,
									@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.update(code, dto, language));
	}
}
