package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.dto.CropBO;
import bg.bulsi.bfsa.dto.ProductionLandBO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@Table(name = "applications_s3365")
public class ApplicationS3365 extends OrderApplication {

//    TODO:Земеделски производител; Булстат (текст)
//     ControctorDTO or Contractor

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "crop_type")
    private CropBO cropType;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "production_ocations")
    private List<ProductionLandBO> productionLocations = new ArrayList<>();
}
