package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.PersonBO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s2695")
public class ApplicationS2695 extends BaseApplication {

    @Column(name = "aviation_operator_name")
    private String aviationOperatorName;

    @Column(name = "aviation_operator_identifier")
    private String aviationOperatorIdentifier;

    @Column(name = "aviation_ch_64_cert_number")
    private String aviationCh64CertNumber;

    @Column(name = "aviation_ch_64_cert_date")
    private OffsetDateTime aviationCh64CertDate;

    @Column(name = "aerial_spray_start_date")
    private OffsetDateTime aerialSprayStartDate;

    @Column(name = "aerial_spray_end_date")
    private OffsetDateTime aerialSprayEndDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_83_certified_person")
    private PersonBO ch83CertifiedPerson;

    @Column(name = "worksite")
    private String worksite;

    @Column(name = "work_land")
    private String workLand;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "applications_s2695_plant_protection_products",
            joinColumns = @JoinColumn(name = "application_s2695_id"),
            inverseJoinColumns = @JoinColumn(name = "applications_s2695_ppp_id")
    )
    private List<ApplicationS2695Ppp> plantProtectionProducts = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private ApplicationS2695Field applicationS2695Field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phenophase_culture_code")
    private Nomenclature phenophaseCulture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aerial_spray_agricultural_group")
    private Classifier aerialSprayAgriculturalGroup;

    @NotAudited
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "applications_s2695_subagricultures",
            joinColumns = @JoinColumn(name = "applications_s2695_id"),
            inverseJoinColumns = @JoinColumn(name = "subagriculture_code")
    )
    private List<Classifier> subAgricultures = new ArrayList<>();

}
