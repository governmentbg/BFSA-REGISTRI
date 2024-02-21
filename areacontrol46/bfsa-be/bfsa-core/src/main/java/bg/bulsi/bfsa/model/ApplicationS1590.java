package bg.bulsi.bfsa.model;

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
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
@Table(name = "applications_s1590")
public class ApplicationS1590 extends OrderApplication {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "warehouse_address_id", referencedColumnName = "id")
    private Address warehouseAddress;

    //    2. Лица със сертификат по чл. 83 от ЗЗР, свързани с дейностите
    //    2.1 Лице, отговорно за дейността
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ch_83_certified_person_id")
    private Contractor ch83CertifiedPerson;

    //    2.2. Лица, извършващи дейността:
    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_83_certified_persons")
    private List<PersonBO> ch83CertifiedPersons = new ArrayList<>();
}
