package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.ContractorVehicle;
import bg.bulsi.bfsa.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorVehicleDTO {

    private VehicleDTO vehicle;
    private ServiceType serviceType;
    private String ownerTypeCode;
    private String ownerTypeName;

    public static ContractorVehicleDTO of(final ContractorVehicle source, final Language language) {
        ContractorVehicleDTO dto = ContractorVehicleDTO.builder()
                .serviceType(source.getServiceType()).build();

        if (source.getVehicle() != null) {
            dto.setVehicle(VehicleDTO.of(source.getVehicle(), language));
        }
        if (source.getOwnerType() != null) {
            dto.setOwnerTypeCode(source.getOwnerType().getCode());
            dto.setOwnerTypeName(source.getOwnerType().getI18n(language).getName());
        }
        return dto;
    }

    public static List<ContractorVehicleDTO> of(final List<ContractorVehicle> sources, final Language language) {
        return sources.stream().map(cv -> ContractorVehicleDTO.of(cv, language)).collect(Collectors.toList());
    }
}
