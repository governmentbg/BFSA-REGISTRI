package bg.bulsi.bfsa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class PersonBO {
    private String identifier;
    private String fullName;
    private String name;
    private String surname;
    private String familyName;
    private String degree;
    private String email;
    private String phone;
    private String description;

    public String getFullName() {
        return name + (StringUtils.hasText(surname) ? " " + surname : "") + " " + familyName;
    }
}
