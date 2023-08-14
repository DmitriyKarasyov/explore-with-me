package ru.practicum.main_service.exception;

public class IncorrectRequestException extends EWMException {

    public IncorrectRequestException(String message) {
        super(message, "Incorrectly made request.");
    }
}
