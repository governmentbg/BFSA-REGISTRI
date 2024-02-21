package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.FacilityStatus;
import bg.bulsi.bfsa.enums.ServiceType;
import bg.bulsi.bfsa.model.BaseApprovalDocument;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorFacility;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Language;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
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
public class ContractorFacilityDTO {
    private Long id;
    private String regNumber;
    private LocalDate regDate;
    private ServiceType serviceType;

    private Long contractorId;
    private String contractorIdentifier;
    private String contractorFullName;

    private Long facilityId;
    private String activityTypeCode;
    private String activityTypeName;
    private String subActivityTypeCode;
    private String subActivityTypeName;
    private String facilityTypeCode;
    private String facilityTypeName;
    private String facilityPaperRegNumbers;
    private LocalDate facilityPaperRegDate;
    private FacilityStatus facilityStatus;
    private AddressDTO facilityAddress;

    @Builder.Default
    private List<KeyValueDTO> registers = new ArrayList<>();

    public static ContractorFacilityDTO of(final ContractorFacility source, final Language language) {
        ContractorFacilityDTO dto = new ContractorFacilityDTO();

        dto.setId(source.getId());
        dto.setServiceType(source.getServiceType());

        Facility facility = source.getFacility();
        if (facility != null) {
            dto.setFacilityId(facility.getId());
            dto.setRegNumber(facility.getRegNumber());
            dto.setRegDate(dto.getRegDate());
            dto.setFacilityStatus(facility.getStatus());

            if (facility.getActivityType() != null) {
                dto.setActivityTypeCode(facility.getActivityType().getCode());
                dto.setActivityTypeName(facility.getActivityType().getI18n(language).getName());
            }
            if (facility.getSubActivityType() != null) {
                dto.setSubActivityTypeCode(facility.getSubActivityType().getCode());
                dto.setSubActivityTypeName(facility.getSubActivityType().getI18n(language).getName());
            }
            if (facility.getFacilityType() != null) {
                dto.setFacilityTypeCode(facility.getFacilityType().getCode());
                dto.setFacilityTypeName(facility.getFacilityType().getI18n(language).getName());
            }

            if (facility.getAddress() != null) {
                dto.setFacilityAddress(AddressDTO.of(facility.getAddress(), language));
            }

            if (!CollectionUtils.isEmpty(facility.getFacilityPapers())) {
                dto.setFacilityPaperRegNumbers(facility.getFacilityPapers()
                        .stream().map(BaseApprovalDocument::getRegNumber).collect(Collectors.joining(", ")));
            }

            if (!CollectionUtils.isEmpty(facility.getRegisters())) {
                for (Classifier c : facility.getRegisters()) {
                    dto.getRegisters().add(KeyValueDTO.builder().code(c.getCode()).name(c.getI18n(language).getName()).build());
                }
            }
        }

        Contractor contractor = source.getContractor();
        if (contractor != null) {
            dto.setContractorId(contractor.getId());
            dto.setContractorIdentifier(dto.getContractorIdentifier());
            dto.setContractorFullName(dto.getContractorFullName());
        }

        return dto;
    }

    public static List<ContractorFacilityDTO> of(final List<ContractorFacility> source, final Language language) {
        return source.stream().map(e -> of(e, language)).collect(Collectors.toList());
    }
}
