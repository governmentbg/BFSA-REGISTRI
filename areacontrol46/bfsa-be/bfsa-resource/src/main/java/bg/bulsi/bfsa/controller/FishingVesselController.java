package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.FishingVesselDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.FishingVesselService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/fishing-vessels")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FishingVesselController {

    private final FishingVesselService service;
    private final LanguageService languageService;

    @GetMapping(path = "/")
    public Page<FishingVesselDTO> findAll(final Pageable pageable,
                                          @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return service.findAll(pageable, language);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> findById(@PathVariable final Long id,
                                      @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.getById(id, language));
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody final FishingVesselDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(FishingVesselDTO.of(service.create(dto), language));
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
                                    @RequestBody @Valid final FishingVesselDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.update(id, dto, language));
    }

    @GetMapping(path = "/registration-number/{regNumber}")
    public ResponseEntity<?> findByRegNumber(@PathVariable final String regNumber,
                                             @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(FishingVesselDTO.of(service.findByRegNumber(regNumber), language));
    }

    @GetMapping(path = "/external-marking/{externalMarking}")
    public ResponseEntity<?> findByExternalMarking(@PathVariable final String externalMarking,
                                                   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(FishingVesselDTO.of(service.findByExternalMarking(externalMarking), language));
    }

//    @GetMapping(path = "/hull-length/{hullLength}")
//    public Page<FishingVesselDTO> findByHullLength(@PathVariable final Double hullLength,
//                                                   final Pageable pageable,
//                                                   @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        Language language = LocaleUtil.getLanguage(locale, languageService);
//        return service.findByHullLength(hullLength, pageable).map(FishingVesselDTO::of);
//    }

    @GetMapping(path = "/entry-number/{entryNumber}")
    public ResponseEntity<?> findByEntryNumber(@PathVariable final String entryNumber,
                                               @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(FishingVesselDTO.of(service.findByEntryNumber(entryNumber), language));
    }

//    @GetMapping(path = "/date/")
//    public Page<FishingVesselDTO> findByDate(@RequestParam(value = "start") final String start,
//                                             @RequestParam(value = "end") final String end,
//                                             final Pageable pageable,
//                                             @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        LocalDate s = LocalDate.parse(start);
//        LocalDate e = LocalDate.parse(end);
//        // TODO: Change to LONG, current formant 'yyyy-MM-dd'
//        Language language = LocaleUtil.getLanguage(locale, languageService);
//        return service.findByDate(s, e, pageable).map(FishingVesselDTO::of);
//    }

//    @GetMapping(path = "/branch/{branchId}")
//    public Page<FishingVesselDTO> findByBranch(@PathVariable final Long branchId,
//                                               final Pageable pageable,
//                                               @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        return service.findByBranch(branchId, pageable).map(FishingVesselDTO::of);
//    }
//
//    @GetMapping(path = "/assignment-type/{code}")
//    public Page<FishingVesselDTO> findByAssignmentType(@PathVariable final String code,
//                                                       final Pageable pageable,
//                                                       @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        return service.findByAssignmentType(code, pageable).map(FishingVesselDTO::of);
//    }

//    @Secured({RolesConstants.ROLE_ADMIN})
//    @GetMapping(path = "/{id}/history")
//    public ResponseEntity<?> history(@PathVariable Long id,
//                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        return ResponseEntity.ok(service.findRevisions(id));
//    }
}