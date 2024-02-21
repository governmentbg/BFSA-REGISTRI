package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.FileDTO;
import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.InspectionService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/inspections")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class InspectionController {

    private final InspectionService service;
    private final LanguageService languageService;

    @GetMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> findById(@PathVariable final Long id,
                                      @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findById(id, language));
    }

    @GetMapping
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale,
                                     final Pageable pageable) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findAll(pageable, language));
    }

    @GetMapping("/{id}/attachments")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> attachments(@PathVariable final Long id) {
        return ResponseEntity.ok(FileDTO.of(service.attachments(id)));
    }

//    @PostMapping("/create")
//    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
//    public ResponseEntity<?> create(@RequestBody final InspectionDTO dto,
//                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        Language language = LocaleUtil.getLanguage(locale, languageService);
//        return ResponseEntity.ok().body(InspectionDTO.of(service.
//                create(InspectionDTO.to(dto)), language));
//    }

    @PostMapping("/{id}/{docTypeCode}/attach")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> attach(@PathVariable @NotNull final Long id,
                                    @PathVariable @NotNull final String docTypeCode,
                                    @RequestParam("file") final MultipartFile file) {
        return ResponseEntity.ok().body(FileDTO.of(service.attach(id, file, docTypeCode)));
    }

    @PutMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
                                    @RequestBody final InspectionDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(InspectionDTO.ofWithFacilityAndRecordIds(service.update(id, dto, language)));
    }
}
