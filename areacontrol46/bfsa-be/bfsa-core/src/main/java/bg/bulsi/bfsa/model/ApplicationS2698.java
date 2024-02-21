package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.AdjuvantBO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "applications_s2698")
public class ApplicationS2698 extends OrderApplication {

    @Column(name = "adjuvant_name")
    private String adjuvantName;
    @Column(name = "adjuvant_name_lat")
    private String adjuvantNameLat;

    @Column(name = "manufacturer_name")
    private String manufacturerName;
    @Column(name = "manufacturer_identifier")
    private String manufacturerIdentifier;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Contractor supplier;

    @Column(name = "effects")
    private String effects;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    // Nomenclature 03800 Functions of plat protection product
    private List<Nomenclature> pppFunctions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjuvant_product_formulation_type", referencedColumnName = "code")
    private Nomenclature adjuvantProductFormulationType;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ingredients")
    private List<AdjuvantBO> ingredients = new ArrayList<>();

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "applications")
    private List<AdjuvantBO> applications = new ArrayList<>();
}
