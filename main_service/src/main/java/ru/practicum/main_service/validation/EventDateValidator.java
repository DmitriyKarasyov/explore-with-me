package ru.practicum.main_service.validation;

import ru.practicum.main_service.common.EWMDateFormatter;
import ru.practicum.main_service.exception.ConditionViolationException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventDateValidator implements ConstraintValidator<ValidEventDate, String> {

    @Override
    public void initialize(ValidEventDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String stringEventDate, ConstraintValidatorContext constraintValidatorContext) {
        return validate(stringEventDate);
    }

    public static boolean validate(String stringEventDate) {
        if (stringEventDate == null) {
            return true;
        }
        LocalDateTime eventDate = LocalDateTime.parse(stringEventDate, EWMDateFormatter.FORMATTER);
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ConditionViolationException(
                    String.format("Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s",
                            eventDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        }
        return true;
    }
}
