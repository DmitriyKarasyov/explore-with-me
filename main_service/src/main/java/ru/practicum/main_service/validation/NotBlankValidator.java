package ru.practicum.main_service.validation;

import ru.practicum.main_service.exception.IncorrectRequestException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotBlankValidator implements ConstraintValidator<NotBlank, Object> {

    public String fieldName;

    @Override
    public void initialize(NotBlank constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.fieldName = constraintAnnotation.fieldName();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o == null) {
            throw new IncorrectRequestException(String.format("Field: %s. Error: must not be blank. Value: null",
                    fieldName));
        }
        if (o.getClass() == String.class) {
            if (((String) o).isBlank()) {
                throw new IncorrectRequestException(String.format("Field: %s. Error: must not be blank. Value: %s",
                        fieldName, o));
            }
        }
        return true;
    }
}
