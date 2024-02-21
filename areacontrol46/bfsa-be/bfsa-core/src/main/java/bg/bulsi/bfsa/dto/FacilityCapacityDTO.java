package bg.bulsi.bfsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityCapacityDTO {
    private String product;
    private Integer quantity;
    private String materialCode;
    private String unitCode;
    private String rawMilkTypeCode;
    private Double fridgeCapacity;
}
