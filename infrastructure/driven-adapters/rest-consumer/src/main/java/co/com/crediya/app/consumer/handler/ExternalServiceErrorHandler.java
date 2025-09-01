package co.com.crediya.app.consumer.handler;

import co.com.crediya.app.model.constants.ExternalService;
import co.com.crediya.app.model.exception.common.ExternalServiceException;
import co.com.crediya.app.model.exception.loanapplication.UserNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class ExternalServiceErrorHandler {

    public  Throwable handleServiceError(ExternalService service, Throwable error) {
        if (error instanceof WebClientResponseException.NotFound && service == ExternalService.AUTHENTICATION) {
            return new UserNotFoundException();
        } else {
            return new ExternalServiceException(service.getDisplayName());
        }
    }
}