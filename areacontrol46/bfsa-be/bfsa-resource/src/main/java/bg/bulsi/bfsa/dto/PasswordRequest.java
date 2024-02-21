package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.validator.PasswordMatches;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@PasswordMatches
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {

	@Size(min = 6, message = "{Size.userDto.password}")
	private String password;
	@NotEmpty
	private String matchingPassword;

}
