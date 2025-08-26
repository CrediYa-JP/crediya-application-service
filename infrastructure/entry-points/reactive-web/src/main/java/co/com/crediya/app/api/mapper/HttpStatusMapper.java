package co.com.crediya.app.api.mapper;

import co.com.crediya.app.model.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;


public class HttpStatusMapper {
    private HttpStatusMapper() {
    }

    // HttpStatusMapper.java
    public static HttpStatus mapToHttpStatus(ErrorCode errorCode) {
        return switch(errorCode.getCode()) {
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}