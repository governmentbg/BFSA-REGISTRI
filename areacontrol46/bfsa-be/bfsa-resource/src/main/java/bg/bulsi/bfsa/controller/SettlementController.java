package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApiListResponse;
import bg.bulsi.bfsa.dto.SettlementDTO;
import bg.bulsi.bfsa.dto.SettlementVO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.SettlementService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/settlements")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SettlementController {

    private final SettlementService service;
    private final LanguageService languageService;

    @GetMapping("/")
    public ResponseEntity<?> findAllParentsWithoutSubs(final Pageable pageable) {
        return ResponseEntity.ok(service.findAllParentsWithoutSubs(pageable));
    }

    @GetMapping
    public ResponseEntity<?> findAllParents() {
        List<SettlementDTO> list = service.findAllParents();
        return ResponseEntity.ok().body(
                ApiListResponse.builder()
                        .results(list)
                        .totalCount(list.size())
                        .build()
        );
    }

    @GetMapping("/parents")
    public ResponseEntity<?> findAllSettlementVO() {
        List<SettlementVO> list = service.findAllSettlementVO();
        return ResponseEntity.ok().body(
                ApiListResponse.builder()
                        .results(list)
                        .totalCount(list.size())
                        .build()
        );
    }

    @GetMapping("/{code}/municipality-settlements")
    public ResponseEntity<?> findAllMunicipalitySettlements(@PathVariable final String code) {
        List<SettlementVO> list = service.findAllRegionSettlements(code);
        return ResponseEntity.ok().body(
                ApiListResponse.builder()
                        .results(list)
                        .totalCount(list.size())
                        .build()
        );
    }

    @GetMapping("/{code}/region-settlements")
    public ResponseEntity<?> findAllRegionSettlements(@PathVariable final String code) {
        List<SettlementVO> list = service.findAllMunicipalitySettlements(code);
        return ResponseEntity.ok().body(
                ApiListResponse.builder()
                        .results(list)
                        .totalCount(list.size())
                        .build()
        );
    }

    @GetMapping("/{parentCode}/sub-settlements")
    public ResponseEntity<?> findAllByParentCodeSettlementVO(@PathVariable final String parentCode) {
        List<SettlementVO> list = service.findAllByParentCodeSettlementVO(parentCode);
        return ResponseEntity.ok().body(
                ApiListResponse.builder()
                        .results(list)
                        .totalCount(list.size())
                        .build()
        );
    }

    @GetMapping(path = "/{code}")
    public ResponseEntity<?> findByCode(@PathVariable final String code) {
        return ResponseEntity.ok().body(service.findByCodeDto(code));
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @PostMapping(path = "/create")
    public ResponseEntity<?> create(@RequestBody final SettlementDTO dto) {
        return ResponseEntity.ok().body(SettlementDTO.of(service.create(SettlementDTO.to(dto))));
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @PutMapping(path = "/{code}")
    public ResponseEntity<?> update(@PathVariable @NotNull final String code,
                                    @RequestBody @Valid final SettlementDTO dto) {
        return ResponseEntity.ok().body(service.updateDTO(code, dto));
    }

    @Secured({RolesConstants.ROLE_ADMIN})
    @GetMapping(path = "/{code}/history")
    public ResponseEntity<?> history(@PathVariable String code) {
        return ResponseEntity.ok(service.findRevisions(code));
    }

    @GetMapping(path = "/{code}/info")
    public ResponseEntity<?> getInfo(@PathVariable @NotNull final String code,
                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok().body(service.getInfo(code, language.getLanguageId()));
    }

    @GetMapping(path = "/{code}/get-codes")
    public ResponseEntity<?> getCodes(@PathVariable @NotNull final String code) {
        return ResponseEntity.ok().body(service.getCodes(code));
    }

    @GetMapping(params = {"q"})
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT, RolesConstants.ROLE_CONTRACTOR, RolesConstants.ROLE_FINANCE})
    public ResponseEntity<?> search(@RequestParam(name = "q", required = false) final String param) {
        List<SettlementVO> respond = service.search(param);
        if (respond == null || respond.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(respond);
    }
}