package co.com.crediya.app.model.exception;

import co.com.crediya.app.model.exception.errorcode.ErrorCode;


public abstract class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }


    protected BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    protected BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }

    protected BusinessException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCode() {
        return errorCode.getCode();
    }
}