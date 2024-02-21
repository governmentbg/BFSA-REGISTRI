package bg.bulsi.bfsa.controller;


import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.FacilityDTO;
import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.FacilityService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/facilities")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FacilityController {
    private final FacilityService service;
    private final LanguageService languageService;

//    @Secured({RolesConstants.ROLE_ADMIN})
//    @PostMapping("/create")
//    public ResponseEntity<?> create(@RequestBody final FacilityDTO dto,
//                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        Language language = LocaleUtil.getLanguage(locale, languageService);
//        return ResponseEntity.ok().body(FacilityDTO.of(service.create(FacilityDTO.to(dto, language)), language));
//    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
                                    @RequestBody @Valid final FacilityDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(FacilityDTO.of(service.update(id, dto, language), language));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @GetMapping(path = "/{id}")
    public ResponseEntity<?> findById(@PathVariable final Long id,
                                      @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.getById(id, language));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @GetMapping("/")
    public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        List<FacilityDTO> results = FacilityDTO.of(service.findAll(), language);
        return ResponseEntity.ok().body(
                ApiListResponse.builder()
                        .results(results)
                        .totalCount(results.size())
                        .build()
        );
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @GetMapping(path = "/{id}/history")
    public ResponseEntity<?> history(@PathVariable Long id,
                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findRevisions(id, language));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @PutMapping("/{id}/new-facility-owner/{currentOwnerId}/{newOwnerId}")
    public ResponseEntity<?> changeOwner(@PathVariable @NotNull final Long id,
                                         @PathVariable @NotNull final Long currentOwnerId,
                                         @PathVariable @NotNull final Long newOwnerId,
                                         @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.changeFacilityOwner(id, currentOwnerId, newOwnerId, null, language));
    }

//    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//    @GetMapping(path = "/register-code-and-branch/{registerCode}")
//    public ResponseEntity<?> findByRegisterCodeAndBranch(@PathVariable final String registerCode,
//                                                         @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
//                                                         final Pageable pageable) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (userDetails == null || !userDetails.isEnabled() || !StringUtils.hasText(userDetails.getUsername())) {
//            throw new RuntimeException("Login first");
//        }
//        Language language = LocaleUtil.getLanguage(locale, languageService);
//        return ResponseEntity.ok().body(
//                service.findByRegisterCodeAndBranchId(userDetails.getUsername(), registerCode, language, pageable));
//    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @GetMapping(path = "/contractor-id/{contractorId}")
    public ResponseEntity<?> findAllByContractorId(@PathVariable final Long contractorId,
                                                @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.findAllByContractorId(contractorId, language));
    }

//    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//    @GetMapping(path = "/division-code-and-branch/{divisionCode}")
//    public ResponseEntity<?> findByDivisionCodeAndBranch(@PathVariable final String divisionCode,
//                                                         @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
//                                                         final Pageable pageable) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (userDetails == null || !userDetails.isEnabled() || !StringUtils.hasText(userDetails.getUsername())) {
//            throw new RuntimeException("Login first");
//        }
//        Language language = LocaleUtil.getLanguage(locale, languageService);
//        return ResponseEntity.ok().body(
//                service.findByDivisionCodeAndBranchId(userDetails.getUsername(), divisionCode, language, pageable));
//    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @GetMapping("/{id}/vehicles")
    public ResponseEntity<?> findAllVehicles(@PathVariable final Long id,
                                             @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findAllVehicles(id, language));
    }

    @PostMapping("/{id}/inspection")
    @Secured({RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> createInspection(@PathVariable Long id,
                                              @RequestBody @Valid final InspectionDTO dto,
                                              @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        if (dto.getFacilityId() == null || !id.equals(dto.getFacilityId())) {
            throw new RuntimeException("Facility ID is required.");
        }
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.createInspection(dto, language));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @PutMapping("/{id}/complete-inspection")
    public ResponseEntity<?> completeInspection(@PathVariable final Long id, @RequestBody final InspectionDTO dto,
                                                @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.completeInspection(id, dto, language));
    }

    // findAllByContractorIdAndServiceType
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @GetMapping(path = "/contractor-id/{id}/service-type/{serviceType}")
    public ResponseEntity<?> findAllByContractorIdAndServiceType(@PathVariable final Long id,
                                                                 @PathVariable final String serviceType,
                                                         @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null || !userDetails.isEnabled() || !StringUtils.hasText(userDetails.getUsername())) {
            throw new RuntimeException("Login first");
        }
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(
                service.findAllByContractorIdAndServiceType(id, serviceType, language));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @PutMapping(path = "/{id}/update-status")
    public ResponseEntity<?> updateStatus(@PathVariable @NotNull final Long id,
                                    @RequestBody @Valid final KeyValueDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.updateStatus(id, dto, language));
    }

}
