package co.com.crediya.app.model.exception.loanapplication;

import co.com.crediya.app.model.exception.BusinessException;
import co.com.crediya.app.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.app.model.exception.errorcode.LoanApplicationErrorCode;

public class InvalidLoanTypeException extends BusinessException {

    public InvalidLoanTypeException() {
        super(LoanApplicationErrorCode.INVALID_LOAN_TYPE);
    }

    public InvalidLoanTypeException(String loanType) {
        super(LoanApplicationErrorCode.INVALID_LOAN_TYPE,
                String.format("The provided loan type '%s' is invalid.", loanType));
    }

    public InvalidLoanTypeException(Throwable cause) {
        super(LoanApplicationErrorCode.INVALID_LOAN_TYPE, "The provided loan type is invalid.", cause);
    }
}