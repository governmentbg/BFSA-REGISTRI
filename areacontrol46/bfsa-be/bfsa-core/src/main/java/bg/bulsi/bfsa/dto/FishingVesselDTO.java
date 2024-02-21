package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.ApplicationS7691FishingVessel;
import bg.bulsi.bfsa.model.FishingVessel;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Nomenclature;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FishingVesselDTO {

    private Long id;
    private LocalDate date;
    private String regNumber;
    private String externalMarking;
    private Boolean enabled;
    private String entryNumber;
    private Double hullLength;
    private String assignmentTypeCode;
    private String assignmentTypeName;
    private String typeCode;
    private String typeName;
    private Long branchId;
    private RevisionMetadataDTO revisionMetadata;

    public static FishingVessel to(FishingVesselDTO source) {
        FishingVessel entity = new FishingVessel();
        BeanUtils.copyProperties(source, entity);

        if (StringUtils.hasText(source.getAssignmentTypeCode())) {
            entity.setAssignmentType(Nomenclature.builder().code(source.getAssignmentTypeCode()).build());
        }
        if (StringUtils.hasText(source.getTypeCode())) {
            entity.setType(Nomenclature.builder().code(source.getTypeCode()).build());
        }

        return entity;
    }

    public static List<FishingVessel> to(final List<FishingVesselDTO> source) {
        return source.stream().map(d -> to(d)).collect(Collectors.toList());
    }

    public static FishingVesselDTO of(final FishingVessel source, final Language language) {
        FishingVesselDTO dto = new FishingVesselDTO();
        BeanUtils.copyProperties(source, dto);

        if (source.getBranch() != null) {
            dto.setBranchId(source.getBranch().getId());
        }
        if (source.getAssignmentType() != null) {
            dto.setAssignmentTypeCode(source.getAssignmentType().getCode());
            dto.setAssignmentTypeName(source.getAssignmentType().getI18n(language).getName());
        }
        if (source.getType() != null) {
            dto.setTypeCode(source.getType().getCode());
            dto.setTypeName(source.getType().getI18n(language).getName());
        }

        return dto;
    }

    public static List<FishingVesselDTO> of(final List<FishingVessel> source, final Language language) {
        return source.stream().map(e -> of(e, language)).collect(Collectors.toList());
    }

    public static List<FishingVesselDTO> ofS7691FishingVessel(final List<ApplicationS7691FishingVessel> source, final Language language) {
        return source.stream().map(e -> of(e.getFishingVessel(), language)).collect(Collectors.toList());
    }
}
