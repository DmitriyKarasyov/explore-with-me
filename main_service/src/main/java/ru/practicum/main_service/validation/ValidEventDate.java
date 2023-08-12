package ru.practicum.main_service.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = {EventDateValidator.class})
@Retention(RUNTIME)
@Target(FIELD)
public @interface ValidEventDate {

    String message() default "Wrong length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
