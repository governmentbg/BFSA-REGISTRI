package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.BranchI18n;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Settlement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchDTO {

    private Long id;
    private String identifier;
    private String settlementCode;
    private String email;
    private String phone1;
    private String phone2;
    private String phone3;
    private String name;
    private String address;
    private String description;
    private Boolean main;
    private Boolean enabled;
    // TODO In case we need all users fields.
    //  In other case just put all userId in List<String>
    private List<UserDTO> users = new ArrayList<>();

    public static BranchDTO of(final Branch source, final Language language) {
        BranchDTO dto = new BranchDTO();
        BeanUtils.copyProperties(source, dto);

        dto.setSettlementCode(source.getSettlement() != null ? source.getSettlement().getCode() : null);
        // failed to lazily initialize a collection of role: bg.bulsi.bfsa.model.Branch.users,
        // could not initialize proxy - no Session
        if (!CollectionUtils.isEmpty(source.getUsers())) {
            dto.getUsers().addAll(UserDTO.of(source.getUsers()));
        }

        BranchI18n i18n = source.getI18n(language);
        dto.setName(i18n.getName());
        dto.setAddress(i18n.getAddress());
        dto.setDescription(i18n.getDescription());

        return dto;
    }

    public static List<BranchDTO> of(final List<Branch> source, final Language language) {
        return source.stream().map(t -> of(t, language)).collect(Collectors.toList());
    }

    public static Branch to(final BranchDTO source, final Language language) {
        Branch entity = new Branch();
        BeanUtils.copyProperties(source, entity);

        if (StringUtils.hasText(source.getSettlementCode())) {
            entity.setSettlement(Settlement.builder().code(source.getSettlementCode()).build());
        }
        entity.getI18ns().add(
                new BranchI18n(source.getName(), source.getAddress(), source.getDescription(), entity, language)
        );
        return entity;
    }
}
