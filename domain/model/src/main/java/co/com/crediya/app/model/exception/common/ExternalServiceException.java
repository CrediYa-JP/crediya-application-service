package co.com.crediya.app.model.exception.common;

import co.com.crediya.app.model.exception.BusinessException;
import co.com.crediya.app.model.exception.constants.BusinessErrorCodes;
import co.com.crediya.app.model.exception.errorcode.ExternalServiceErrorCode;


// Nueva excepción
public class ExternalServiceException extends BusinessException {

    public ExternalServiceException(String serviceName) {
        super(ExternalServiceErrorCode.EXTERNAL_SERVICE_ERROR,
                    String.format("Error occurred while communicating with external service: %s", serviceName));
    }
}

