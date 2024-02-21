package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApplicationS2701DTO;
import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.model.ContractorPaper;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.security.RolesConstants;
import bg.bulsi.bfsa.service.ApplicationS2701Service;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/s2701-applications")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS2701Controller {

    private final ApplicationS2701Service service;
    private final LanguageService languageService;

    @GetMapping("/{recordId}")
    public ResponseEntity<?> findByRecordId(@PathVariable final Long recordId,
                                      @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findByRecordId(recordId, language));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody final ApplicationS2701DTO dto,
                                      @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        Record record = service.register(dto, null, language);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .recordStatus(record.getStatus())
                .build());
    }

    @PutMapping("/{recordId}/education")
    public ResponseEntity<?> education(@PathVariable final Long recordId,
                                       @RequestBody final ApplicationS2701DTO dto,
                                       @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        Record record = service.education(recordId, dto, language);
        ContractorPaper paper = record.getContractorPaper();

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .recordStatus(record.getStatus())
                .regNumber(paper.getRegNumber())
                .regDate(paper.getRegDate())
                .approvalDocumentStatus(paper.getStatus())
                .build());
    }

    @PutMapping("/{recordId}/refuse")
    public ResponseEntity<?> refuse(@PathVariable final Long recordId) {
        Record record = service.refuse(recordId);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .recordStatus(record.getStatus())
                .build());
    }

    @PutMapping("/{recordId}/approve")
    public ResponseEntity<?> approve(@PathVariable final Long recordId,
                                     @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);

        Record record = service.approve(recordId, language);
        ContractorPaper paper = record.getContractorPaper();

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .regNumber(paper.getRegNumber())
                .regDate(paper.getRegDate())
                .approvalDocumentStatus(paper.getStatus())
                .build());
    }

    @PutMapping("/{recordId}/for-correction")
    public ResponseEntity<?> forCorrection(@PathVariable final Long recordId,
                                           @RequestBody final KeyValueDTO dto,
                                           @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {

        if (dto.getId() == null || dto.getId() <= 0 || !recordId.equals(dto.getId())) {
            throw new RuntimeException("Record id is required.");
        }
        Language language = LocaleUtil.getLanguage(locale, languageService);
        Record record = service.forCorrection(recordId, dto.getDescription(), language);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .regNumber(record.getEntryNumber())
                .regDate(record.getContractorPaper().getRegDate())
                .approvalDocumentStatus(record.getContractorPaper().getStatus())
                .build());
    }

    @PostMapping("/{recordId}/attach")
    @Secured({RolesConstants.ROLE_ADMIN, RolesConstants.ROLE_EXPERT})
    public ResponseEntity<?> attach(@PathVariable @NotNull final Long recordId,
                                    @NotNull @RequestParam("file") final MultipartFile file) {

        Record record = service.attach(recordId, file);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .regNumber(record.getEntryNumber())
                .build());
    }
}
