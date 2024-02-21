package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.PersonBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "applications_s2700")
public class ApplicationS2700 extends OrderApplication {

    // 1. Лица извършващи консултантски услуги
    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ch_83_certified_persons")
    private List<PersonBO> ch83CertifiedPersons = new ArrayList<>();
}
