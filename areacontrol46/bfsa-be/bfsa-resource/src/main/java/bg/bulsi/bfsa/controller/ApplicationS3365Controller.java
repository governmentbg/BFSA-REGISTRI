package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApplicationS3365DTO;
import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.service.ApplicationS3365Service;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/s3365-applications")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS3365Controller {
    private final ApplicationS3365Service service;
    private final LanguageService languageService;

    @GetMapping("/{recordId}")
    public ResponseEntity<?> findByRecordId(@PathVariable final Long recordId,
                                            @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findByRecordId(recordId, language));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody final ApplicationS3365DTO dto,
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
    public ResponseEntity<?> approve(@PathVariable final Long recordId) {
        Record record = service.approve(recordId);
        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .build());
    }
}
