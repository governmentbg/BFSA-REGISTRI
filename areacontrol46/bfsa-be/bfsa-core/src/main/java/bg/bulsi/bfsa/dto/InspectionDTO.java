package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.InspectionStatus;
import bg.bulsi.bfsa.enums.InspectionType;
import bg.bulsi.bfsa.enums.RiskLevel;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionDTO {
    private Long id;
    private String description;
    private Long facilityId;
    private String facilityName;
    private Long recordId;
    private LocalDate endDate;
    private InspectionType inspectionType;
    private RiskLevel riskLevel;
    private InspectionStatus status;
    private String applicantIdentifier;
    private String applicantFullName;
    private Long vehicleId;
    private String vehicleBrandMode;
    private Long contractorId;

    @Builder.Default
    private List<Long> users = new ArrayList<>();
    @Builder.Default
    private List<String> reasonsCodes = new ArrayList<>();
    @Builder.Default
    private Set<KeyValueDTO> attachments = new HashSet<>();

    public static Inspection to(final InspectionDTO source, Language language) {
        Inspection entity = new Inspection();
        BeanUtils.copyProperties(source, entity);
        entity.setType(source.getInspectionType() != null
                ? source.getInspectionType()
                : null);
        if (source.getRiskLevel() != null) {
            entity.setRiskLevel(source.getRiskLevel());
        }
        if (StringUtils.hasText(source.getDescription())) {
            entity.getI18n(language).setDescription(source.getDescription());
        }
        entity.setRecord(source.getRecordId() != null && source.getRecordId() > 0
                ? Record.builder().id(source.recordId).build()
                : null);

        return entity;
    }

    public static List<Inspection> to(final List<InspectionDTO> sources, Language language) {
        return sources.stream().map(i -> InspectionDTO.to(i, language)).collect(Collectors.toList());
    }

    public static InspectionDTO of(final Inspection source, final Language language) {
        InspectionDTO dto = baseOf(source, language);

        if (!CollectionUtils.isEmpty(source.getAttachments())) {
            source.getAttachments().forEach(
                    a -> dto.getAttachments().add(KeyValueDTO.builder()
                            .id(a.getId()).name(a.getFileName()).build())
            );
        }
        if (!CollectionUtils.isEmpty(source.getUsers())) {
            dto.setUsers(source.getUsers().stream().map(User::getId).collect(Collectors.toList()));
        }
        if (source.getVehicle() != null && source.getVehicle().getId() != null && source.getVehicle().getId() > 0) {
            dto.setVehicleId(source.getVehicle().getId());
            dto.setVehicleBrandMode(source.getVehicle().getI18n(language).getBrandModel());
        }
        if (source.getFacility() != null && source.getFacility().getId() != null && source.getFacility().getId() > 0) {
            dto.setFacilityId(source.getFacility().getId());
            dto.setFacilityName(source.getFacility().getI18n(language).getName());
        }
        return dto;
    }

    public static InspectionDTO baseOf(final Inspection source, final Language language) {
        InspectionDTO dto = new InspectionDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getI18n(language) != null) {
            dto.setDescription(source.getI18n(language).getDescription());
        }
        if (source.getRiskLevel() != null) {
            dto.setRiskLevel(source.getRiskLevel());
        }
        if (source.getType() != null) {
            dto.setInspectionType(source.getType());
        }
        if (source.getStatus() != null) {
            dto.setStatus(source.getStatus());
        }
        if (!CollectionUtils.isEmpty(source.getReasons())) {
            source.getReasons().forEach(r -> dto.getReasonsCodes().add(r.getCode()));
        }
        if (source.getRecord() != null && source.getRecord().getId() != null && source.getRecord().getId() > 0) {
            dto.setRecordId(source.getRecord().getId());
            if (source.getRecord().getApplicant() != null) {
                dto.setApplicantIdentifier(source.getRecord().getApplicant().getIdentifier());
                dto.setApplicantFullName(source.getRecord().getApplicant().getFullName());
            }
        }
        if (source.getContractor() != null) {
            dto.setContractorId(source.getContractor().getId());
            dto.setApplicantFullName(source.getContractor().getFullName());
            dto.setApplicantIdentifier(source.getContractor().getIdentifier());
        }
        return dto;
    }

    public static InspectionDTO ofWithFacilityAndRecordIds(final Inspection source) {
        InspectionDTO dto = new InspectionDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getFacility() != null && source.getFacility().getId() != null && source.getFacility().getId() > 0) {
            dto.setFacilityId(source.getFacility().getId());
        }
        if (source.getRecord() != null && source.getRecord().getId() != null && source.getRecord().getId() > 0) {
            dto.setRecordId(source.getRecord().getId());
        }
        if (source.getVehicle() != null && source.getVehicle().getId() != null && source.getVehicle().getId() > 0) {
            dto.setVehicleId(source.getVehicle().getId());
        }

        return dto;
    }

    public static List<InspectionDTO> of(final List<Inspection> sources, final Language language) {
        return sources.stream().map(i -> of(i, language)).collect(Collectors.toList());
    }
}
