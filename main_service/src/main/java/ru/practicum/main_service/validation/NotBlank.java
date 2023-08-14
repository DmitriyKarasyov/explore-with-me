package ru.practicum.main_service.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {NotBlankValidator.class})
@Retention(RUNTIME)
@Target(FIELD)
public @interface NotBlank {

    String fieldName();

    String message() default "Field cannot be blank.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
