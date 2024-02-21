package bg.bulsi.bfsa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLocationCoordinateException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8080904742295044297L;

    public InvalidLocationCoordinateException(String msg) {
        super(msg);
    }

}
