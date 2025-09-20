package co.com.crediya.app.model.exception.loanapplication;

import co.com.crediya.app.model.exception.BusinessException;
import co.com.crediya.app.model.exception.errorcode.LoanApplicationErrorCode;

public class ApplicationNotFoundException extends BusinessException {

    public  ApplicationNotFoundException(){
        super(LoanApplicationErrorCode.APPLICATION_NOT_FOUND);
    }
    public ApplicationNotFoundException(Long applicationId){
        super(LoanApplicationErrorCode.APPLICATION_NOT_FOUND,
        String.format("The application with id '%s' was not found.", applicationId));
    }

}