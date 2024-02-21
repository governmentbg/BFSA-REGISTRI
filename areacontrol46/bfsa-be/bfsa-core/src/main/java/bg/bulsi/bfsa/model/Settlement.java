package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.PlaceType;
import bg.bulsi.bfsa.enums.TSB;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "settlements")
public class Settlement extends AuditableEntity {

    @Id
    @Column(name = "code", length = 40)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "name_lat")
    private String nameLat;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "region_name")
    private String regionName;

    @Column(name = "region_name_lat")
    private String regionNameLat;

    @Column(name = "municipality_code")
    private String municipalityCode;

    @Column(name = "municipality_name")
    private String municipalityName;

    @Column(name = "municipality_name_lat")
    private String municipalityNameLat;

    @Enumerated(EnumType.STRING)
    @Column(name = "place_type")
    private PlaceType placeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "tsb")
    private TSB tsb;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "parent_code")
    private String parentCode;

    @Builder.Default
//    @MapsId("parentCode")
    @OneToMany(mappedBy = "parentCode", cascade = CascadeType.ALL)
    private final List<Settlement> subSettlements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code")
    private Country country;
}
