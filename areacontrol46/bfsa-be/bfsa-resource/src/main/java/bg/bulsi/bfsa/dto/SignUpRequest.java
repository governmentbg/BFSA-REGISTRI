package bg.bulsi.bfsa.dto;

import bg.bulsi.bfsa.model.Role;
import bg.bulsi.bfsa.model.User;
import bg.bulsi.bfsa.model.VerificationToken;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest extends PasswordRequest {

	private Long id;
	@NotEmpty
	private String identifier;
	@NotEmpty
	private String email;
	@NotEmpty
	private String fullName;
	@NotEmpty
	private String username;

	public static User buildUser(final SignUpRequest signUpRequest, final Set<Role> roles) {
		User user = new User();
		BeanUtils.copyProperties(signUpRequest, user);
		user.setRoles(roles);
		return user;
	}

	public static VerificationToken buildVerificationToken(final SignUpRequest dto) {
		VerificationToken entity = new VerificationToken();
		BeanUtils.copyProperties(dto, entity);
		return entity;
	}
}
