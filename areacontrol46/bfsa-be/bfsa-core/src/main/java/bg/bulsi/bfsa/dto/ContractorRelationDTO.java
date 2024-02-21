package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.ContractorRelation;
import bg.bulsi.bfsa.model.Nomenclature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorRelationDTO {

    private Long id;
    private Long contractorId;
    private Long relationId;
    private String relationTypeCode;

    public static ContractorRelation to(final ContractorRelationDTO source) {
        ContractorRelation entity = new ContractorRelation();
        BeanUtils.copyProperties(source, entity);

//        entity.setContractorRelationIdentity(new ContractorRelationIdentity(
//                StringUtils.hasText(source.getContractorId()) ? source.getContractorId() : null,
//                StringUtils.hasText(source.getRelationId()) ? source.getRelationId() : null
//        ));
        entity.setContractor(source.getContractorId() != null && source.getContractorId() > 0 ? Contractor.builder().id(source.getContractorId()).build() : null);
        entity.setRelation(source.getRelationId() != null && source.getRelationId() > 0 ? Contractor.builder().id(source.getRelationId()).build() : null);
        entity.setRelationType(StringUtils.hasText(source.getRelationTypeCode()) ? Nomenclature.builder().code(source.getRelationTypeCode()).build() : null);

        return entity;
    }

    public static List<ContractorRelation> to(final List<ContractorRelationDTO> sources) {
        return sources.stream().map(ContractorRelationDTO::to).collect(Collectors.toList());
    }

    public static ContractorRelationDTO of(final ContractorRelation source) {
        ContractorRelationDTO dto = new ContractorRelationDTO();
        if (source.getContractor() != null && source.getContractor().getId() != null && source.getContractor().getId() > 0) {
            dto.setContractorId(source.getContractor().getId());
        }
        if (source.getRelation() != null && source.getRelation().getId() != null && source.getRelation().getId() > 0) {
            dto.setRelationId(source.getRelation().getId());
        }
        if (source.getRelationType() != null && StringUtils.hasText(source.getRelationType().getCode())) {
            dto.setRelationTypeCode(source.getRelationType().getCode());
        }
        return dto;
    }

    public static List<ContractorRelationDTO> of(final List<ContractorRelation> sources) {
        return sources.stream().map(ContractorRelationDTO::of).collect(Collectors.toList());
    }
}
