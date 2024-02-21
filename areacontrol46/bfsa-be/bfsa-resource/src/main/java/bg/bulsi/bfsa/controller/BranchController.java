package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.BranchDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.BranchService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/branches")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BranchController {

    private final BranchService service;
    private final LanguageService languageService;

    @GetMapping("/")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> findAll(@RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        List<BranchDTO> branches = BranchDTO.of(service.findAll(), LocaleUtil.getLanguage(locale, languageService));
        return ResponseEntity.ok()
                .body(ApiListResponse.builder()
                    .results(branches)
                    .totalCount(branches.size())
                    .build());
    }

    @GetMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> getById(@PathVariable final Long id,
                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        return ResponseEntity.ok().body(
                BranchDTO.of(service.findById(id), LocaleUtil.getLanguage(locale, languageService))
        );
    }

    @PostMapping("/create")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> create(@RequestBody final BranchDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(BranchDTO.of(service.create(BranchDTO.to(dto, language)), language));
    }

    @PutMapping("/{id}")
    @Secured({RolesConstants.ROLE_ADMIN})
    public ResponseEntity<?> update(@PathVariable @NotNull final Long id,
                                    @RequestBody final BranchDTO dto,
                                    @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        if (dto.getId() != null && !id.equals(dto.getId())) {
            throw new RuntimeException("Path variable id doesn't match RequestBody parameter id");
        }
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(BranchDTO.of(service.update(id, dto, language), language));
    }
}
