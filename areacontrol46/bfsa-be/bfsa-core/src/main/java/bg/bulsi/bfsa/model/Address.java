package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.ServiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Audited
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "addresses")
public class Address extends BaseEntity {

    @Column(name = "settlement_name")
    private String settlementName;

    @Column(name = "settlement_name_lat")
    private String settlementNameLat;

    @Column(name = "address")
    private String address;

    @Column(name = "land")
    private String land;

    @Column(name = "plotNumber")
    private String plotNumber;

    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "address_lat")
    private String addressLat;

    @Column(name = "phone")
    private String phone;

    @Column(name = "post_code")
    private String postCode;

    @Column(name = "mail")
    private String mail;

    @Column(name = "url")
    private String url;

    @Column(name = "enabled")
    private Boolean enabled;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_code")
    private Settlement settlement;

    @Builder.Default
    @ManyToMany(mappedBy = "addresses", fetch = FetchType.EAGER)
    private List<Contractor> contractors = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_type_code")
    private Nomenclature addressType;

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code")
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type")
    private ServiceType serviceType;
}
