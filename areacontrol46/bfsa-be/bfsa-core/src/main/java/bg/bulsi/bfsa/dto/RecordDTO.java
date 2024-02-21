package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.RecordStatus;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Record;
import bg.bulsi.bfsa.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
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
public class RecordDTO {
    private Long id;
    private String identifier;
    private String entryNumber;
    private LocalDate entryDate;
    private Long contractorId;
    private Long assigneeId;
    private String status;
    private List<Long> inspectionIds;

    public static Record to(final RecordDTO source) {
        Record entity = new Record();
        BeanUtils.copyProperties(source, entity);

        entity.setApplicant(source.getContractorId() != null && source.getContractorId() > 0
                ? Contractor.builder().id(source.getContractorId()).build()
                : null);

        entity.setAssignee(source.getAssigneeId() != null && source.getAssigneeId() > 0
                ? User.builder().id(source.getAssigneeId()).build()
                : null);

        entity.setStatus(StringUtils.hasText(source.getStatus())
                ? RecordStatus.valueOf(source.getStatus())
                : null);

        if (!CollectionUtils.isEmpty(source.getInspectionIds())) {
            source.getInspectionIds().forEach(
                    c -> entity.getInspections().add(Inspection.builder().record(entity).build())
            );
        }

        return entity;
    }

    public static List<Record> to(final List<RecordDTO> sources) {
        return sources.stream().map(RecordDTO::to).collect(Collectors.toList());
    }

    public static RecordDTO of(final Record source) {
        RecordDTO dto = new RecordDTO();
        BeanUtils.copyProperties(source, dto);

        dto.setContractorId(source.getApplicant() != null && source.getApplicant().getId() > 0
                ? source.getApplicant().getId()
                : null);

        dto.setAssigneeId(source.getAssignee() != null && source.getAssignee().getId() > 0
                ? source.getAssignee().getId()
                : null);

        dto.setStatus(source.getStatus().name());

//        if (!CollectionUtils.isEmpty(source.getInspections())) {
//            source.getInspections().forEach(e -> dto.getInspectionIds().add(e.getId()));
//        }

        return dto;
    }

    public static List<RecordDTO> of(final List<Record> sources) {
        return sources.stream().map(RecordDTO::of).collect(Collectors.toList());
    }
}
