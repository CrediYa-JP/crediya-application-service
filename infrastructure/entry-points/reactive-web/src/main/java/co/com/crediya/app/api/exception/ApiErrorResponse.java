package co.com.crediya.app.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private boolean success;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<FieldError> errors;

    @Data
    @Builder
    public static class FieldError {
        private String field;
        private String message;
    }
}