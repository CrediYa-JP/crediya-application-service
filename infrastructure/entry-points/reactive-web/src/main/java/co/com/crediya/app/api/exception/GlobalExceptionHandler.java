package co.com.crediya.app.api.exception;

import co.com.crediya.app.api.mapper.HttpStatusMapper;
import co.com.crediya.app.model.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String VALIDATION_ERROR_CODE = "VAL001";
    private static final String INTERNAL_ERROR_CODE = "SYS001";
    private static final String  MALFORMED_JSON_ERROR_CODE= "REQ001";


    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleBusinessException(BusinessException ex) {
        log.warn("Business rule violation - Code: {}, Message: {}", ex.getCode(), ex.getMessage());

        HttpStatus httpStatus = HttpStatusMapper.mapToHttpStatus(ex.getErrorCode());

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return Mono.just(ResponseEntity.status(httpStatus).body(response));
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleCustomValidationException(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());

        List<ApiErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::buildFieldError)
                .toList();

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(VALIDATION_ERROR_CODE)
                .message("Validation failed")
                .timestamp(LocalDateTime.now())
                .errors(fieldErrors)
                .build();

        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleServerWebInputException(ServerWebInputException ex) {
        log.warn("Malformed request body: {}", ex.getMessage());

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(MALFORMED_JSON_ERROR_CODE)
                .message("Invalid request format")
                .timestamp(LocalDateTime.now())
                .build();

        return Mono.just(ResponseEntity.badRequest().body(response));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiErrorResponse>> handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);

        ApiErrorResponse response = ApiErrorResponse.builder()
                .success(false)
                .code(INTERNAL_ERROR_CODE)
                .message("Internal server error")
                .timestamp(LocalDateTime.now())
                .build();

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }

    private ApiErrorResponse.FieldError buildFieldError(FieldError fieldError) {
        return ApiErrorResponse.FieldError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build();
    }
}