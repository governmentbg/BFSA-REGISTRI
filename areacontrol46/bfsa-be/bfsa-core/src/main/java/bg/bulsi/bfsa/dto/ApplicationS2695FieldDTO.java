package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.ApplicationS2695Field;
import bg.bulsi.bfsa.model.Language;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ApplicationS2695FieldDTO extends BaseApplicationDTO {
    private OffsetDateTime treatmentDate;
    private OffsetDateTime treatmentStartHour;
    private OffsetDateTime treatmentEndHour;
    private Double treatmentArea;
    private Double treatmentDistance;
    private AddressDTO treatmentAddress;
    private List<DistantNeighborSettlementBO> distantNeighborSettlements = new ArrayList<>();

    public static ApplicationS2695Field of(final ApplicationS2695FieldDTO source) {
        ApplicationS2695Field entity = new ApplicationS2695Field();
        BeanUtils.copyProperties(source, entity);

        if (source.getTreatmentAddress() != null) {
            entity.setTreatmentAddress(AddressDTO.to(source.getTreatmentAddress()));
        }

        return entity;
    }

    public static ApplicationS2695FieldDTO of(final ApplicationS2695Field source, final Language language) {
        ApplicationS2695FieldDTO dto = new ApplicationS2695FieldDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getTreatmentAddress() != null) {
            dto.setTreatmentAddress(AddressDTO.of(source.getTreatmentAddress(), language));
        }
        return dto;
    }
}
