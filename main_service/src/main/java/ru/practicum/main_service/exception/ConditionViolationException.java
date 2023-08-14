package ru.practicum.main_service.exception;

public class ConditionViolationException extends EWMException {

    public ConditionViolationException(String message) {
        super(message, "For the requested operation the conditions are not met.");
    }
}
