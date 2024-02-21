package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApplicationS502DTO;
import bg.bulsi.bfsa.dto.ApproveDTO;
import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.RecordDTO;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.ApplicationS502Service;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/s502-applications")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS502Controller {

    private final ApplicationS502Service service;
    private final LanguageService languageService;

    @GetMapping("/{recordId}")
    public ResponseEntity<?> findByRecordId(@PathVariable final Long recordId,
                                      @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findByRecordId(recordId, language));
    }

//    @GetMapping("/applicant/{applicantId}")
//    public ResponseEntity<?> findAllByApplicantId(@PathVariable final Long applicantId,
//                                                  @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
//        Language language = LocaleUtil.getLanguage(locale, languageService);
//        return ResponseEntity.ok(service.findAllByApplicantIdAndStatus(applicantId, ApplicationStatus.APPROVED, language));
//    }

     @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody final ApplicationS502DTO dto,
                                      @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        Record record = service.register(dto, null, language);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .regNumber(record.getEntryNumber())
                .regDate(record.getEntryDate())
                .build());
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    @PutMapping("/{recordId}/send-for-payment")
    public ResponseEntity<?> sendForPayment(@PathVariable final Long recordId,
                                            @RequestParam(name = "recordPrice", required = false) final BigDecimal recordPrice,
                                            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) throws Exception {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        Record record = service.sendForPayment(recordId, recordPrice, language);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .build());
    }

    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_FINANCE})
    @PutMapping("/{recordId}/confirm-payment")
    public ResponseEntity<?> confirmPayment(@PathVariable final Long recordId) {
        return ResponseEntity.ok(RecordDTO.of(service.confirmPayment(recordId)));
    }

    @PutMapping("/{recordId}/approve")
    public ResponseEntity<?> approve(@PathVariable final Long recordId,
                                     @Valid @RequestBody final ApproveDTO dto,
                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);

        Record record = service.approve(recordId, dto, language);
        ContractorPaper paper = record.getContractorPaper();

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .regNumber(paper.getRegNumber())
                .regDate(paper.getRegDate())
                .build());
    }

}