package co.com.crediya.app.model.exception.errorcode;

import co.com.crediya.app.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.app.model.exception.constants.BusinessErrorMessages;
import lombok.Getter;


public enum UnauthorizedOperationErrorCode  implements ErrorCode {

    UNAUTHORIZED_OPERATION(BusinessErrorCodes.UNAUTHORIZED_OPERATION, BusinessErrorMessages.UNAUTHORIZED_OPERATION_MSG);

    private final String code;
    private final String defaultMessage;
    private UnauthorizedOperationErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

}
