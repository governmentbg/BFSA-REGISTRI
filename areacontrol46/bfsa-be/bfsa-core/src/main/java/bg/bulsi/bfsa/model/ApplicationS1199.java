package bg.bulsi.bfsa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
/**
 * S1199:Заявление за вписване в списъка на търговците, които получават пратки храни
 * от животински произход от друга държава-членка на Европейския съюз
 **/
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications_s1199")
public class ApplicationS1199 extends BaseApplication {

    @Column(name = "facility_reg_number")
    private String facilityRegNumber;

    @Column(name = "leased_warehouse_space")
    private Boolean leasedWarehouseSpace;
}
