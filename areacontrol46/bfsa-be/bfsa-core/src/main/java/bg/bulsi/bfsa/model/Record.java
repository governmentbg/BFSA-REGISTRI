package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.NotAudited;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "records")
// Record == Преписка
public class Record extends BaseEntity {
    @Column(name = "identifier")
    private String identifier;

    @Column(name = "entry_number")
    private String entryNumber;

    @Column(name = "entry_date")
    private LocalDate entryDate;

//    @Column(name = "request_date_time")
//    private LocalDateTime requestDateTime;

    @Column(name = "price", precision = 7, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RecordStatus status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    private Contractor requestor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_author_type_code")
    private Nomenclature requestorAuthorType;

    @Column(name = "requestor_power_attorney_number")
    private String requestorPowerAttorneyNumber;

    @Column(name = "requestor_power_attorney_notary")
    private String requestorPowerAttorneyNotary;

    @Column(name = "requestor_power_attorney_date")
    private LocalDate requestorPowerAttorneyDate;

    @Column(name = "requestor_power_attorney_until_date")
    private LocalDate requestorPowerAttorneyUntilDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Contractor applicant;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assignee_id")
    private User assignee;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_paper_id")
    private ContractorPaper contractorPaper;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_paper_id")
    private FacilityPaper facilityPaper;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "directorate_code")
    private Classifier directorate;

    @Builder.Default
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> attachments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<Inspection> inspections = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s16_id")
    private ApplicationS16 applicationS16;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s502_id")
    private ApplicationS502 applicationS502;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s503_id")
    private ApplicationS503 applicationS503;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s1199_id")
    private ApplicationS1199 applicationS1199;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s1366_id")
    private ApplicationS1366 applicationS1366;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s1590_id")
    private ApplicationS1590 applicationS1590;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s1811_id")
    private ApplicationS1811 applicationS1811;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2170_id")
    private ApplicationS2170 applicationS2170;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2272_id")
    private ApplicationS2272 applicationS2272;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2274_id")
    private ApplicationS2274 applicationS2274;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2695_id")
    private ApplicationS2695 applicationS2695;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2697_id")
    private ApplicationS2697 applicationS2697;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2698_id")
    private ApplicationS2698 applicationS2698;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2699_id")
    private ApplicationS2699 applicationS2699;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2700_id")
    private ApplicationS2700 applicationS2700;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2701_id")
    private ApplicationS2701 applicationS2701;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2702_id")
    private ApplicationS2702 applicationS2702;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2711_id")
    private ApplicationS2711 applicationS2711;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s2869_id")
    private ApplicationS2869 applicationS2869;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s3125_id")
    private ApplicationS3125 applicationS3125;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s3180_id")
    private ApplicationS3180 applicationS3180;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s3181_id")
    private ApplicationS3181 applicationS3181;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s3182_id")
    private ApplicationS3182 applicationS3182;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s3201_id")
    private ApplicationS3201 applicationS3201;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s3362_id")
    private ApplicationS3362 applicationS3362;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s3363_id")
    private ApplicationS3363 applicationS3363;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s3365_id")
    private ApplicationS3365 applicationS3365;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s7691_id")
    private ApplicationS7691 applicationS7691;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s7692_id")
    private ApplicationS7692 applicationS7692;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s7693_id")
    private ApplicationS7693 applicationS7693;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s7694_id")
    private ApplicationS7694 applicationS7694;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s7695_id")
    private ApplicationS7695 applicationS7695;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_s7696_id")
    private ApplicationS7696 applicationS7696;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "errors")
    private List<String> errors = new ArrayList<>();

//    @OneToOne(targetEntity = Role.class, fetch = FetchType.EAGER)
//    @JoinColumn(name = "role_id", nullable = false)
//    private Role role;
}