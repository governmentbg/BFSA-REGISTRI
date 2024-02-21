package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.dto.index.DocWS;
import bg.bulsi.bfsa.dto.index.FileWS;
import bg.bulsi.bfsa.enums.EducationalDocumentType;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import generated.ServiceRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

/**
 * ЗАЯВЛЕНИЕ
 * за издаване на сертификат за използване на продукти за растителна защита
 * от професионална категория на употреба по чл. 83 и чл. 87 от Закона за защита на растенията
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2701DTO extends BaseApplicationDTO {

    // Данни за физическото лице
    private Boolean requestorApplicantMatch;

    private EducationalDocumentType educationalDocumentType;
    private String educationalDocumentNumber;
    private LocalDate educationalDocumentDate;
    private String educationalInstitution;
    private String description;
    private LocalDate discrepancyUntilDate;

    public static ApplicationS2701DTO of(final Record source, final Language language) {
        ApplicationS2701DTO dto = new ApplicationS2701DTO();

//        --- Set Requestor, Applicant and base fields ---
        dto.ofRecordBase(source, language);

        if (source.getApplicationS2701() != null) {
            dto.setEducationalDocumentType(source.getApplicationS2701().getEducationalDocumentType());
            dto.setEducationalDocumentNumber(source.getApplicationS2701().getEducationalDocumentNumber());
            dto.setEducationalDocumentDate(source.getApplicationS2701().getEducationalDocumentDate());
            dto.setEducationalInstitution(source.getApplicationS2701().getEducationalInstitution());
            dto.setDescription(source.getApplicationS2701().getDescription());
            dto.setCertificateImage(source.getApplicationS2701().getCertificateImage());
        }

        if (source.getContractorPaper() != null) {
            dto.setApprovalDocumentStatus(source.getContractorPaper().getStatus());
        }

        return dto;
    }

    public static ApplicationS2701DTO ofServiceRequest(final ServiceRequest serviceRequest, DocWS docInfo) {
        ApplicationS2701DTO dto = new ApplicationS2701DTO();

        dto.of(serviceRequest, docInfo);

        // TODO refactor to get certificate image for 2701 in right way
        FileWS certificateImage = docInfo.getFiles().stream()
                .filter(f -> f != null && "image/png".equals(f.getFileType()))
                .findFirst().orElse(null);

        if (certificateImage != null) {
            dto.setCertificateImage(certificateImage.getFileContent());
        }

        if (serviceRequest.getSpecificContent() instanceof LinkedHashMap<?, ?> sc) {
            if (sc.get("specificContent") instanceof LinkedHashMap<?, ?> sc0) {
                sc = sc0;
            }

            dto.setEducationalDocumentType(EducationalDocumentType.valueOf(
                    ((LinkedHashMap<?, ?>) sc.get("educationalDocumentType")).get("key").toString())
            );
            dto.setEducationalDocumentNumber(sc.get("educationalDocumentNumber").toString());
            dto.setEducationalDocumentDate(parseDate(sc.get("educationalDocumentDate").toString()));
            dto.setEducationalInstitution(sc.get("educationalInstitution").toString());
        }

        return dto;
    }

    private static LocalDate parseDate(String date) {
        return StringUtils.hasText(date)
                ? ZonedDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate()
                : null;
    }
}
