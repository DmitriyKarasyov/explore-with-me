package ru.practicum.main_service.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main_service.common.EWMDateFormatter;
import ru.practicum.main_service.error.ApiError;
import ru.practicum.main_service.exception.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError badRequestHandler(IncorrectRequestException e) {
        return makeApiError(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundHandler(NotFoundException e) {
        return makeApiError(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError constraintViolationHandler(EWMConstraintViolationException e) {
        return makeApiError(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError conditionViolationHandler(ConditionViolationException e) {
        return makeApiError(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError numberFormatHandler(NumberFormatException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason("Incorrectly made request.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().format(EWMDateFormatter.FORMATTER))
                .build();
    }

    public ApiError makeApiError(HttpStatus httpStatus, EWMException e) {
        return ApiError.builder()
                .message(e.getMessage())
                .reason(e.getReason())
                .status(httpStatus.toString())
                .timestamp(LocalDateTime.now().format(EWMDateFormatter.FORMATTER))
                .build();
    }
}
