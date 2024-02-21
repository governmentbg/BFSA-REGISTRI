package bg.bulsi.bfsa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.text.MessageFormat;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -7292273141168527527L;

    private static final String ENTITY_NOT_FOUND = "Entity ''{0}'' with id/code=''{1}'' not found";

    public EntityNotFoundException(Class entityClass, Long id) {
        super(MessageFormat.format(ENTITY_NOT_FOUND, entityClass.getName(), id.toString()));
    }

    public EntityNotFoundException(Class entityClass, Object id) {
        super(MessageFormat.format(ENTITY_NOT_FOUND, entityClass.getName(), id));
    }

    public EntityNotFoundException(Class entityClass, String id, Throwable cause) {
        super(MessageFormat.format(ENTITY_NOT_FOUND, entityClass.getName(), id), cause);
    }

    public EntityNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
