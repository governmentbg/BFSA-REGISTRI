package bg.bulsi.bfsa.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s1366")
public class ApplicationS1366 extends BaseApplication {

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_address")
    private String recipientAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_country_code")
    private Country recipientCountry;

    // Nomenclature-library 02200: Operator Type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_type_code")
    private Nomenclature applicantType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id")
    private Facility facility;

    // Nomenclature-library : Border Crossing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "border_crossing_code")
    private Nomenclature borderCrossing;

    @Builder.Default
    @OneToMany(mappedBy = "applicationS1366", cascade = CascadeType.ALL)
    private List<ApplicationS1366Product> applicationS1366Products = new ArrayList<>();

}
