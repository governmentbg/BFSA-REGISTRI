package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.AddressBO;
import bg.bulsi.bfsa.dto.SubstanceBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
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
@Table(name = "applications_s2697")
public class ApplicationS2697 extends BaseApplication {

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "substances")
    private List<SubstanceBO> substances = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @JoinColumn(name = "warehouse_address")
    private AddressBO warehouseAddress;
}
