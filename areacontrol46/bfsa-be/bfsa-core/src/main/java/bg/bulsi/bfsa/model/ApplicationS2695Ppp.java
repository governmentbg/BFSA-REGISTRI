package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.PppPestBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applications_s2695_ppp")
public class ApplicationS2695Ppp extends BaseApplication {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppp_function_code")
    private Nomenclature pppFunction;

    @Column(name = "ppp_name")
    private String pppName;

    @Column(name = "ppp_aerial_spray")
    private Boolean pppAerialSpray = false;

    @Column(name = "ppp_purchase")
    private String pppPurchase;

    @Column(name = "ppp_dose")
    private Double pppDose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ppp_unit_code")
    private Nomenclature pppUnit;

    @Column(name = "ppp_quarantine_period")
    Integer pppQuarantinePeriod;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ppp_pest")
    private List<PppPestBO> pppPests;
}
