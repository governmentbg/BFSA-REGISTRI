package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.ContractorPaperDTO;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.ContractorPaperService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/contractor-papers")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ContractorPaperController {

	private final ContractorPaperService service;
	private final LanguageService languageService;

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping("/")
	public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		List<ContractorPaperDTO> results = service.findAllDTOs(language);
		return ResponseEntity.ok().body(
				ApiListResponse.builder()
						.results(results)
						.totalCount(results.size())
						.build()
		);
	}

//	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//	@GetMapping(path = "/{id}")
//	public ResponseEntity<?> findById(@PathVariable final Long id,
//									  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//		Language language = LocaleUtil.getLanguage(locale, languageService);
//		return ResponseEntity.ok().body(service.getById(id, language));
//	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping(path = "/{contractorId}/{serviceType}")
	public ResponseEntity<?> getByContractorIdAndServiceTypeAndApprovalDocumentStatusIsActive(
			@PathVariable final Long contractorId,
			@PathVariable final ServiceType serviceType,
			@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.getByContractorIdAndServiceTypeAndApprovalDocumentStatusIsActive(contractorId, serviceType, language));
	}

	@Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
	@GetMapping(path = "/{contractorId}")
	public ResponseEntity<?> getByContractorIdAndApprovalDocumentStatusIsActive(
			@PathVariable final Long contractorId,
			@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
		Language language = LocaleUtil.getLanguage(locale, languageService);
		return ResponseEntity.ok().body(service.getByContractorIdAndApprovalDocumentStatusIsActive(contractorId, language));
	}
}
