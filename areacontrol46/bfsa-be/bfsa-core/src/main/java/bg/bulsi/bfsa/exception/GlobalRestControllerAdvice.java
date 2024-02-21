package bg.bulsi.bfsa.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            final HttpServletRequest req,
            final IllegalArgumentException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(
            final HttpServletRequest req,
            final RuntimeException ex) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

	@ExceptionHandler(InvalidLocationCoordinateException.class)
    public ResponseEntity<Object> handleInvalidLocationCoordinateException(
            final HttpServletRequest req,
            final InvalidLocationCoordinateException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            final HttpServletRequest req,
            final EntityNotFoundException ex) {
        final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<Object> handleEntityAlreadyExistException(
            final HttpServletRequest req,
            final EntityAlreadyExistException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Object> handleFileStorageException(
            final HttpServletRequest req,
            final EntityNotFoundException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(
            MaxUploadSizeExceededException ex,
            HttpServletRequest req) {
        final HttpStatus httpStatus = HttpStatus.PAYLOAD_TOO_LARGE;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, "The file you are attempting to attach exceeds the 16MB size limit.");
        return ResponseEntity.status(httpStatus).body(map);
    }

//    @ExceptionHandler(UploadException.class)
//    public ResponseEntity<UploadErrorDTO> handleUploadException(
//            final HttpServletRequest req,
//            final UploadException ex) {
//        final UploadErrorDTO uploadErrorDTO = UploadErrorDTO.of(ex);
//        log.info("handleUploadException request request {} {}", req.getMethod(), req.getRequestURI());
//        log.info("handleUploadException exception", ex);
//        return new ResponseEntity<>(uploadErrorDTO, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<Object> handleInvalidUserDataException(
            final HttpServletRequest req,
            final InvalidLocationCoordinateException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    private static Map<String, String> getPopulatedErrorMap(HttpServletRequest req, Exception ex, HttpStatus httpStatus, String message) {
        log.info("handleError request {} {}", req.getMethod(), req.getRequestURI());
        log.info("handleError httpStatus {}", httpStatus);
        log.info("handleError exception", ex);
        final Map<String, String> map = new HashMap<>();
        map.put("status", String.valueOf(httpStatus.value()));
        map.put("path", req.getRequestURI());
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("exceptionCode", ex.getClass().getSimpleName());
        map.put("error", httpStatus.getReasonPhrase());
        map.put("exception", ex.getClass().getName());
        map.put("message", message);
        return map;
    }

}
