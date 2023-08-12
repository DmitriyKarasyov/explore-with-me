package ru.practicum.main_service.exception;

public class EWMConstraintViolationException extends EWMException {
    public EWMConstraintViolationException(String message) {
        super(message, "Integrity constraint has been violated.");
    }

}

