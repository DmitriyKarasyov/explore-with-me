package ru.practicum.main_service.exception;

public class EWMException extends RuntimeException {
    private final String reason;

    public EWMException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
