package co.com.crediya.auth.api.util;

import co.com.crediya.auth.api.exception.ValidationException;
import lombok.NoArgsConstructor;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

@NoArgsConstructor
public final class ValidationUtil {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    public static <T> Mono<T> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(object, object.getClass().getSimpleName());

            violations.forEach(violation ->
                    bindingResult.addError(new FieldError(
                            bindingResult.getObjectName(),
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    ))
            );

            return Mono.error(new ValidationException(bindingResult));
        }

        return Mono.just(object);
    }
}