package co.com.crediya.app.model.exception.errorcode;

import co.com.crediya.app.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.app.model.exception.constants.BusinessErrorMessages;

public enum ExternalServiceErrorCode implements ErrorCode {
    EXTERNAL_SERVICE_ERROR(BusinessErrorCodes.EXTERNAL_SERVICE , BusinessErrorMessages.USER_NOT_FOUND_MSG);

    private final String code;
    private final String defaultMessage;


    private ExternalServiceErrorCode(String code, String defaultMessage) {
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
