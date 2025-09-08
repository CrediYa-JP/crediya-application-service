package co.com.crediya.app.api.mapper;

import co.com.crediya.app.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.app.model.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;


public class HttpStatusMapper {
    private HttpStatusMapper() {
    }

    // HttpStatusMapper.java
    public static HttpStatus mapToHttpStatus(ErrorCode errorCode) {
        return switch(errorCode.getCode()) {
            case BusinessErrorCodes.INVALID_LOAN_TYPE -> HttpStatus.BAD_REQUEST;
            case BusinessErrorCodes.UNAUTHORIZED_OPERATION, BusinessErrorCodes.USER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}