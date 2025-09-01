package co.com.crediya.app.model.exception.loanapplication;

import co.com.crediya.app.model.exception.BusinessException;
import co.com.crediya.app.model.exception.constants.BusinessErrorMessages;
import co.com.crediya.app.model.exception.errorcode.ErrorCode;
import co.com.crediya.app.model.exception.errorcode.LoanApplicationErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(LoanApplicationErrorCode.USER_NOT_FOUND, BusinessErrorMessages.USER_NOT_FOUND_MSG);
    }

    public UserNotFoundException(String email) {
        super(LoanApplicationErrorCode.USER_NOT_FOUND, "User with email " + email + " not found in authentication system");
    }

    public UserNotFoundException(Throwable cause) {
        super(LoanApplicationErrorCode.USER_NOT_FOUND, cause);
    }

    public UserNotFoundException(String customMessage, Throwable cause) {
        super(LoanApplicationErrorCode.USER_NOT_FOUND, customMessage, cause);
    }
}
