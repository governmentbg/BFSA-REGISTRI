package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.FileDTO;
import bg.bulsi.bfsa.dto.InspectionDTO;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.service.RecordService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/records")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class RecordController {

    private final RecordService service;
    private final LanguageService languageService;

    @GetMapping("/branch/{branchId}")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT, RolesConstants.ROLE_FINANCE})
    public ResponseEntity<?> findAllByBranch(@PathVariable final Long branchId, final Pageable pageable) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null || !userDetails.isEnabled() || !StringUtils.hasText(userDetails.getUsername())) {
            throw new RuntimeException("Login first");
        }
        return ResponseEntity.ok(service.findAllByBranchBaseApplicationDTO(userDetails.getUsername(), branchId, pageable));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(RecordDTO.of(service.findById(id)));
    }

    @GetMapping
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT, RolesConstants.ROLE_FINANCE})
    public ResponseEntity<?> search(@RequestParam(name = "q", required = false) final String param,
                                    @RequestParam(name = "recordStatus", required = false) final RecordStatus recordStatus,
                                    @RequestParam(name = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") final LocalDate date,
                                    final Pageable pageable) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetails == null || !userDetails.isEnabled() || !StringUtils.hasText(userDetails.getUsername())) {
            throw new RuntimeException("Login first");
        }

        return ResponseEntity.ok(service.search(userDetails.getUsername(), param, recordStatus, date, pageable));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @PutMapping("/{id}/send-for-payment")
    public ResponseEntity<?> sendForPayment(@PathVariable final Long id,
                                            @RequestParam(name = "recordPrice", required = false) final BigDecimal recordPrice,
                                            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) throws Exception {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        Record record = service.sendForPayment(id, recordPrice, language);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .build());
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_FINANCE})
    @PutMapping("/{id}/confirm-payment")
    public ResponseEntity<?> confirmPayment(@PathVariable final Long id) {
        return ResponseEntity.ok(RecordDTO.of(service.confirmPayment(id)));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @PostMapping("/{id}/create-inspection")
    public ResponseEntity<?> createInspection(@PathVariable final Long id, @RequestBody final InspectionDTO dto,
                                              @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.createInspection(id, dto, language));
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @PutMapping("/{id}/complete-inspection")
    public ResponseEntity<?> completeInspection(@PathVariable final Long id, @RequestBody final InspectionDTO dto,
                                                @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(RecordDTO.of(service.completeInspection(id, dto, language)));
    }

    @GetMapping("/{id}/attachments")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> attachments(@PathVariable final Long id) {
        return ResponseEntity.ok(FileDTO.of(service.attachments(id)));
    }
}
