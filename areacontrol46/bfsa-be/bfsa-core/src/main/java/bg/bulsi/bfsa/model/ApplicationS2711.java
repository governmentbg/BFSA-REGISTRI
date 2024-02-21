package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.PersonBO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s2711")
public class ApplicationS2711 extends BaseApplication {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_type_code")
    private Nomenclature facilityType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "facility_address")
    private AddressBO facilityAddress;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ch83_certified_person_id")
    private Contractor ch83CertifiedPerson;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_83_certified_persons")
    private List<PersonBO> ch83CertifiedPersons = new ArrayList<>();
}