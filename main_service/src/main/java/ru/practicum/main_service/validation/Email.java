package ru.practicum.main_service.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {EwmEmailValidator.class})
@Retention(RUNTIME)
@Target(FIELD)
public @interface Email {

    String message() default "Not email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
