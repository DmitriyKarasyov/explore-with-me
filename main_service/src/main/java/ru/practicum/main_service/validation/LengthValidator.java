package ru.practicum.main_service.validation;

import ru.practicum.main_service.exception.IncorrectRequestException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LengthValidator implements ConstraintValidator<Length, String> {

    public String fieldName;
    public int min;
    public int max;

    @Override
    public void initialize(Length constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.fieldName = constraintAnnotation.fieldName();
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        if (string == null) {
            return true;
        }
        if (string.length() < min || string.length() > max) {
            throw new IncorrectRequestException(
                    String.format("Field: %s. Minimum length is %d, maximum length is %d. Length: %d",
                            fieldName, min, max, string.length()));
        }
        return true;
    }
}
