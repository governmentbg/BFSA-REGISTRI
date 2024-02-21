package bg.bulsi.bfsa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.text.MessageFormat;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityAlreadyExistException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1156620787871905689L;

    private static final String ENTITY_ALREADY_EXIST ="Entity ''{0}'' with identifier=''{1}'' already exist";

    public EntityAlreadyExistException(Class entityClass, String id) {
        super(MessageFormat.format(ENTITY_ALREADY_EXIST, entityClass.getName(), id));
    }

    public EntityAlreadyExistException(Class entityClass, Object id) {
        super(MessageFormat.format(ENTITY_ALREADY_EXIST, entityClass.getName(), id));
    }

    public EntityAlreadyExistException(Class entityClass, String id, Throwable cause) {
        super(MessageFormat.format(ENTITY_ALREADY_EXIST, entityClass.getName(), id), cause);
    }

    public EntityAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}
