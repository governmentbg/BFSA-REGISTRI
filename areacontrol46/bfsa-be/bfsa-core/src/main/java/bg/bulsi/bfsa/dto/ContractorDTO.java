package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.enums.EntityType;
import bg.bulsi.bfsa.model.Branch;
import bg.bulsi.bfsa.model.Classifier;
import bg.bulsi.bfsa.model.Contractor;
import bg.bulsi.bfsa.model.Facility;
import bg.bulsi.bfsa.model.Language;
import bg.bulsi.bfsa.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractorDTO {

    private String name;
    private String surname;
    private String familyName;
    private String fullName;
    private String degree;
    private String type; // 1 for contractor; 0 for User
    private EntityType entityType;
    private Long id;
    private String email;
    private String phone;
    private String username;
    private String password;
    private String identifier;
    private Boolean enabled;
    private Boolean farmer;
    private List<String> roles= new ArrayList<>();;
    private List<Long> facilityIds= new ArrayList<>();;
    private List<ContractorRelationDTO> contractorRelations = new ArrayList<>();
    private RevisionMetadataDTO revisionMetadata;
    private List<String> relatedActivityCategories = new ArrayList<>();
    private AddressDTO address;
    private Long branchId;
    private String registerCode;
    private String contractorActivityTypeCode;

    @Builder.Default
    private List<KeyValueDTO> registerCodes = new ArrayList<>();

    public static Contractor to(final ContractorDTO source) {
        Contractor entity = new Contractor();
        BeanUtils.copyProperties(source, entity);

        if (!CollectionUtils.isEmpty(source.getRoles())) {
            source.getRoles().forEach(r ->
                    entity.getRoles()
                            .add(Role.builder().name(r).contractors(List.of(entity)).build()));
        }
        if (!CollectionUtils.isEmpty(source.getContractorRelations())) {
            entity.getContractorRelations()
                    .addAll(ContractorRelationDTO.to(source.getContractorRelations()));
        }
        if (!CollectionUtils.isEmpty(source.getFacilityIds())) {
            source.getFacilityIds().forEach(fId ->
                    entity.addContractorFacility(Facility.builder().id(fId).build(), null));
        }
        entity.setBranch(source.getBranchId() != null && source.getBranchId() > 0
                ? Branch.builder().id(source.getBranchId()).build()
                : null);

        if (!CollectionUtils.isEmpty(source.getRegisterCodes())) {
            for (KeyValueDTO dto : source.getRegisterCodes()) {
                entity.getRegisters().add(Classifier.builder().code(dto.getCode()).build());
            }
        }

        if (source.getEntityType() != null) {
            entity.setEntityType(source.getEntityType());
        }

        return entity;
    }

    public static ContractorDTO of(final UserDetailsDTO source) {
        ContractorDTO dto = new ContractorDTO();
        BeanUtils.copyProperties(source, dto);

        if (!CollectionUtils.isEmpty(source.getAuthorities())) {
            dto.setRoles(source.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }
        return dto;
    }

    public static ContractorDTO of(final Contractor source, final Language language) {
        ContractorDTO dto = new ContractorDTO();
        BeanUtils.copyProperties(source, dto);
        dto.setType("1");

        if (!CollectionUtils.isEmpty(source.getRoles())) {
            dto.setRoles(source.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        }
        if (!CollectionUtils.isEmpty(source.getContractorRelations())) {
            dto.getContractorRelations().addAll(ContractorRelationDTO.of(source.getContractorRelations()));
        }
        if (!CollectionUtils.isEmpty(source.getContractorFacilities())) {
            dto.setFacilityIds(source.getContractorFacilities().stream().map(cf -> cf.getFacility().getId()).collect(Collectors.toList()));
        }
        if (source.getBranch() != null) {
            dto.setBranchId(source.getBranch().getId());
        }
        if (!CollectionUtils.isEmpty(source.getRegisters())) {
            for (Classifier register : source.getRegisters()) {
                dto.getRegisterCodes().add(KeyValueDTO.builder().code(register.getCode()).name(register.getI18n(language).getName()).build());
            }
        }
        if (source.getContractorActivityType() != null) {
            dto.setContractorActivityTypeCode(source.getContractorActivityType().getCode());
        }
        if (source.getEntityType() != null) {
            dto.setEntityType(source.getEntityType());
        }

        return dto;
    }

    public static List<ContractorDTO> of(final List<Contractor> source, final Language language) {
        return source.stream().map(c -> of(c, language)).collect(Collectors.toList());
    }

    public String assembleFullName() {
        if (StringUtils.hasText(fullName)) {
            return fullName;
        }

        StringJoiner joiner = new StringJoiner(" ");

        if (name != null && !name.isEmpty()) {
            joiner.add(name);
        }
        if (surname != null && !surname.isEmpty()) {
            joiner.add(surname);
        }
        if (familyName != null && !familyName.isEmpty()) {
            joiner.add(familyName);
        }

        return joiner.toString();
    }
}
