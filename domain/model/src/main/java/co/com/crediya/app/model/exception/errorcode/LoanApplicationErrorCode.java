package co.com.crediya.app.model.exception.errorcode;

import co.com.crediya.app.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.app.model.exception.constants.BusinessErrorMessages;

public enum LoanApplicationErrorCode  implements ErrorCode {
      INVALID_LOAN_TYPE(BusinessErrorCodes.INVALID_LOAN_TYPE, BusinessErrorMessages.INVALID_LOAN_TYPE_MSG),
      USER_NOT_FOUND(BusinessErrorCodes.USER_NOT_FOUND, BusinessErrorMessages.USER_NOT_FOUND_MSG);

    private final String code;
    private final String defaultMessage;

    private LoanApplicationErrorCode(String code, String defaultMessage) {
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
