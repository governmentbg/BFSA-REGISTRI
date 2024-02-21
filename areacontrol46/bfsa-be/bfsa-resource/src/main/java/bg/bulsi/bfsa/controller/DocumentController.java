package bg.bulsi.bfsa.controller;

import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.service.DocumentService;
import bg.bulsi.bfsa.service.LanguageService;
import bg.bulsi.bfsa.util.LocaleUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/documents")
@SecurityRequirement(name = "bfsaapi")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DocumentController {

    private final DocumentService service;
    private final LanguageService languageService;

    @GetMapping(path = "/{recordId}/export-paper")
    public ResponseEntity<Resource> exportPaper(@PathVariable final Long recordId,
                                                @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) throws IOException {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        Resource resource = service.exportPaper(recordId, language);

        HttpHeaders headers = new HttpHeaders();
        String fileName = "record_" + recordId + "_" + resource.getDescription();

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        headers.add("File-Name", fileName);
        headers.add("Access-Control-Expose-Headers", "File-Name");

        log.debug("\n\nFile name: {}\n\n", fileName);
        log.debug("\n\nFile: {}\n\n", resource);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @GetMapping(path = "/{recordId}/export-order")
    public ResponseEntity<Resource> exportOrder(@PathVariable final Long recordId,
                                                @RequestHeader(HttpHeaders.ACCEPT_LANGUAGE) final Locale locale) throws IOException {
        Language language = LocaleUtil.getLanguage(locale, languageService);
        Resource resource = service.exportOrder(recordId, language);

        HttpHeaders headers = new HttpHeaders();
        String fileName = "record_" + recordId + "_" + resource.getDescription();

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        headers.add("File-Name", fileName);
        headers.add("Access-Control-Expose-Headers", "File-Name");

        log.debug("\n\nFile name: {}\n\n", fileName);
        log.debug("\n\nFile: {}\n\n", resource);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

}
