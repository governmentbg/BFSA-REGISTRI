package bg.bulsi.bfsa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class FacilityVO {
    private Long id;
    private String name;
    private String description;
    private String email;
    private String phone1;
    private String phone2;
    private Boolean enabled;
    private String addressId;
    private Long contractorId;
    private String registerCode;
    private Long branchId;

    public FacilityVO(
            Long id,
            String name,
            String description,
            String mail,
            String phone1,
            String phone2,
            Boolean enabled,
            Long contractorId,
            String registerCode,
            Long branchId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.email = mail;
        this.phone1 = phone1;
        this.phone2 = phone2;
        this.enabled = enabled;
        this.contractorId = contractorId;
        this.registerCode = registerCode;
        this.branchId = branchId;
    }
}
