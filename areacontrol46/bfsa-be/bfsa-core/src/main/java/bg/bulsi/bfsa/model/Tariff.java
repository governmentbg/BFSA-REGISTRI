package bg.bulsi.bfsa.model;

import bg.bulsi.bfsa.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tariffs")
public class Tariff extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", unique = true)
    private ServiceType serviceType;

    @NotNull
    @Column(name = "price", precision = 7, scale = 2)
    private BigDecimal price;

//    @NotNull
//    @Column(name = "valid_from")
//    private LocalDate validFrom;
//
//    @NotNull
//    @Column(name = "valid_to")
//    private LocalDate validTo;
//
//    @Column(name = "enabled")
//    private Boolean enabled;
}
