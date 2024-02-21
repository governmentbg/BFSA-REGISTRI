package bg.bulsi.bfsa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason="User doesn't have rights for requested resource!")
public class ForbiddenException extends RuntimeException {

	private static final long serialVersionUID = 7531024396445305539L;

}
