package bg.bulsi.bfsa.dto.reports;

import bg.bulsi.bfsa.dto.AddressDTO;
import bg.bulsi.bfsa.dto.ContractorFacilityDTO;
import bg.bulsi.bfsa.dto.ContractorPaperDTO;
import bg.bulsi.bfsa.dto.ContractorVehicleDTO;
import bg.bulsi.bfsa.dto.KeyValueDTO;
import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.util.Constants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ContractorInfoDTO {

    private String identifier;
    private String fullName;

    private AddressDTO mainAddress;
    private AddressDTO correspondenceAddress;

    private List<KeyValueDTO> registers = new ArrayList<>();
    private List<ContractorPaperDTO> approvalDocuments = new ArrayList<>();
    private List<ContractorFacilityDTO> facilities = new ArrayList<>();
    private List<ContractorVehicleDTO> contractorVehicles = new ArrayList<>();

    public static ContractorInfoDTO of(final Contractor source, final Language language) {
        ContractorInfoDTO dto = new ContractorInfoDTO();

        dto.setIdentifier(source.getIdentifier());
        dto.setFullName(source.getFullName());

        if (!CollectionUtils.isEmpty(source.getRegisters())) {
            for (Classifier register : source.getRegisters()) {
                dto.getRegisters().add(KeyValueDTO.builder()
                        .code(register.getCode())
                        .name(register.getI18n(language).getName()).build());
            }
        }

        if (!CollectionUtils.isEmpty(source.getAddresses())) {
            String mainAddressTypeCode = EntityType.PHYSICAL.equals(source.getEntityType())
                    ? Constants.ADDRESS_TYPE_PERMANENT_ADDRESS_CODE
                    : Constants.ADDRESS_TYPE_HEAD_OFFICE_CODE;
            dto.setMainAddress(AddressDTO.of(source.getAddresses().stream()
                    .filter(a -> mainAddressTypeCode.equals(a.getAddressType().getCode()))
                    .findFirst().orElse(null), language));

            dto.setCorrespondenceAddress(AddressDTO.of(source.getAddresses().stream()
                    .filter(a -> Constants.ADDRESS_TYPE_CORRESPONDENCE_CODE.equals(a.getAddressType().getCode()))
                    .findFirst().orElse(null), language));
        }

        if (!CollectionUtils.isEmpty(source.getContractorPapers())) {
            dto.setApprovalDocuments(ContractorPaperDTO.of(source.getContractorPapers(), language));
        }

        if (!CollectionUtils.isEmpty(source.getContractorFacilities())) {
            dto.setFacilities(ContractorFacilityDTO.of(source.getContractorFacilities(), language));
        }

        if (!CollectionUtils.isEmpty(source.getContractorPapers())) {
            dto.setApprovalDocuments(ContractorPaperDTO.of(source.getContractorPapers(), language));
        }

        if (!CollectionUtils.isEmpty(source.getContractorVehicles())) {
            dto.setContractorVehicles(ContractorVehicleDTO.of(source.getContractorVehicles(), language));
        }

        return dto;
    }

    public static List<ContractorInfoDTO> of(final List<Contractor> source, final Language language) {
        return source.stream().map(c -> of(c, language)).collect(Collectors.toList());
    }
}
