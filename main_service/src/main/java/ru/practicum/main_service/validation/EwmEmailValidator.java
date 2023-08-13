package ru.practicum.main_service.validation;

import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import ru.practicum.main_service.exception.IncorrectRequestException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EwmEmailValidator implements ConstraintValidator<Email, String> {

    @Override
    public void initialize(Email constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        EmailValidator emailValidator = new EmailValidator();
        if (!emailValidator.isValid(string, constraintValidatorContext)) {
            throw new IncorrectRequestException("Field: email. Incorrect email.");
        }
        return true;
    }
}
