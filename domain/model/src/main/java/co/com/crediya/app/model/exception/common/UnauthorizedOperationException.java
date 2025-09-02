package co.com.crediya.app.model.exception.common;

import co.com.crediya.app.model.exception.BusinessException;
import co.com.crediya.app.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.app.model.exception.constants.BusinessErrorMessages;
import co.com.crediya.app.model.exception.errorcode.UnauthorizedOperationErrorCode;

public class UnauthorizedOperationException extends BusinessException {
    public UnauthorizedOperationException() {
        super(UnauthorizedOperationErrorCode.UNAUTHORIZED_OPERATION, BusinessErrorMessages.UNAUTHORIZED_OPERATION_MSG);
    }
}
