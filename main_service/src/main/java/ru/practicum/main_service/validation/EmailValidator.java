package ru.practicum.main_service.validation;

import ru.practicum.main_service.exception.IncorrectRequestException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<Email, String> {

    @Override
    public void initialize(Email constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        String regex = "^(.+)@(.+)$";
        Pattern regexPattern = Pattern.compile(regex);
        Matcher matcher = regexPattern.matcher(string);
        if (!matcher.matches()) {
            throw new IncorrectRequestException("Field: email. Incorrect email.");
        }
        return true;
    }
}
