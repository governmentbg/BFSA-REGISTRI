package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Inspection;
import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String type; // 1 for contractor; 0 for User
    private Long id;
    private String email;
    private String fullName;
    @NotNull
    private String username;
    private String password;
    private String identifier;
    private boolean enabled;
    private Long branchId;
    private List<String> roles;
    private RevisionMetadataDTO revisionMetadata;
    private List<Long> inspections;
    private String directorateCode;

    public static User to(final UserDTO source) {
        User entity = new User();
        BeanUtils.copyProperties(source, entity);

        if (source.getBranchId() != null && source.getBranchId() > 0) {
            entity.setBranch(Branch.builder().id(source.branchId).build());
        }
        if (!CollectionUtils.isEmpty(source.getRoles())) {
            source.getRoles().forEach(r -> entity.getRoles()
                    .add(Role.builder().name(r).users(List.of(entity)).build()));
        }
        if (!CollectionUtils.isEmpty(source.getInspections())) {
            source.getInspections().forEach(i -> entity.getInspections()
                    .add(Inspection.builder().id(i).users(List.of(entity)).build()));
        }

        entity.setDirectorate(StringUtils.hasText(source.directorateCode)
                ? Classifier.builder().code(source.getDirectorateCode()).build()
                : null);

        return entity;
    }

    public static UserDTO of(final UserDetailsDTO userDetailsDTO) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(userDetailsDTO, dto);

        if (!CollectionUtils.isEmpty(userDetailsDTO.getAuthorities())) {
            dto.setRoles(userDetailsDTO.getAuthorities()
                    .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        return dto;
    }

    public static UserDTO of(final User source) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(source, dto);
        dto.setType("0");

        if (!CollectionUtils.isEmpty(source.getRoles())) {
            dto.setRoles(source.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        }
//        if (!CollectionUtils.isEmpty(source.getInspections())) {
//            dto.setInspections(source.getInspections().stream().map(Inspection::getId).collect(Collectors.toList()));
//        }
        if (source.getBranch() != null) {
            dto.setBranchId(source.getBranch().getId());
        }
        if (source.getDirectorate() != null) {
            dto.setDirectorateCode(source.getDirectorate().getCode());
        }

        return dto;
    }

    public static List<UserDTO> of(final List<User> sources) {
        return sources.stream().map(UserDTO::of).collect(Collectors.toList());
    }
}
