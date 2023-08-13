package ru.practicum.main_service.exception;

public class NotFoundException extends EWMException {

    public NotFoundException(String message) {
        super(message, "The required object was not found.");
    }
}
