package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.dto.ApplicationS3181DTO;
import bg.bulsi.bfsa.dto.BaseApplicationDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.service.ApplicationS3181Service;
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
@RequestMapping("/s3181-applications")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ApplicationS3181Controller {

    private final ApplicationS3181Service service;
    private final LanguageService languageService;

    @GetMapping("/{recordId}")
    public ResponseEntity<?> findByRecordId(@PathVariable final Long recordId,
                                      @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        return ResponseEntity.ok(service.findByRecordId(recordId, language));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody final ApplicationS3181DTO dto,
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

    @PutMapping("/{recordId}/approve")
    public ResponseEntity<?> approve(@PathVariable final Long recordId) {
        Record record = service.approve(recordId);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .regNumber(record.getEntryNumber())
                .build());
    }

    @PutMapping("/{recordId}/refuse")
    public ResponseEntity<?> refuse(@PathVariable final Long recordId) {
        Record record = service.refuse(recordId);

        return ResponseEntity.ok(BaseApplicationDTO.builder()
                .recordId(record.getId())
                .serviceType(record.getServiceType())
                .regNumber(record.getEntryNumber())
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
                .build());
    }
}
