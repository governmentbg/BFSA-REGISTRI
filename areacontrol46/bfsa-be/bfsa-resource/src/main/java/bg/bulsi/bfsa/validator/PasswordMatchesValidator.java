package bg.bulsi.bfsa.validator;

import bg.bulsi.bfsa.dto.PasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, PasswordRequest> {

	@Override
	public boolean isValid(final PasswordRequest request, final ConstraintValidatorContext context) {
		return request.getPassword().equals(request.getMatchingPassword());
	}

}
