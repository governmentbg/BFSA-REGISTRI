package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.EducationalDocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications_s2701")
public class ApplicationS2701 extends BaseApplication {
    @Column(name = "requestor_applicant_match")
    private Boolean requestorApplicantMatch;

    @Enumerated(EnumType.STRING)
    @Column(name = "educational_document_type")
    private EducationalDocumentType educationalDocumentType;

    @Column(name = "educational_document_number")
    private String educationalDocumentNumber;

    @Column(name = "educational_institution")
    private String educationalInstitution;

    @Column(name = "description")
    private String description;

    @Column(name = "educational_document_date")
    private LocalDate educationalDocumentDate;

    @Column(name = "discrepancy_until_date")
    private LocalDate discrepancyUntilDate;

    @Lob
    @Column(name = "certificate_image", length = 10000)
    @JdbcTypeCode(Types.VARBINARY)
    private byte[] certificateImage;
}
