package bg.bulsi.bfsa.exception;

import org.springframework.security.core.AuthenticationException;

import java.io.Serial;

public class UserAlreadyExistAuthenticationException extends AuthenticationException {

	@Serial
	private static final long serialVersionUID = 8618968484093616142L;

	public UserAlreadyExistAuthenticationException(final String msg) {
        super(msg);
    }

}
