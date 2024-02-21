package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.ReportService;
import bg.bulsi.bfsa.util.LocaleUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ReportController {

	private final ReportService service;

	private final LanguageService languageService;

	@GetMapping("/contractor/{identifier}/info")
	public ResponseEntity<?> contractorInfo(@PathVariable final String identifier,
											@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.getContractorInfo(identifier, language));
	}

	@GetMapping("/contractor/{identifier}/vehicles")
	public ResponseEntity<?> contractorVehicles(@PathVariable final String identifier,
											@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.getContractorVehicles(identifier, language));
	}

	@GetMapping("/contractor/{identifier}/food-supplements")
	public ResponseEntity<?> contractorFoodSupplements(@PathVariable final String identifier,
												@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale, final Pageable pageable) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok(service.findAllContractorFoodSupplements(identifier, language, pageable));
	}

}
