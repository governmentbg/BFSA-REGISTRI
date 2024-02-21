package bg.bulsi.bfsa.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public class OrderApplication extends BaseApplication {
    @Column(name = "order_number")
    private String orderNumber;
    @Column(name = "order_date")
    private LocalDate orderDate;
}
