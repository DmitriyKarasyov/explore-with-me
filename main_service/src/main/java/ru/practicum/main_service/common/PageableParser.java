package ru.practicum.main_service.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.exception.IncorrectRequestException;

public class PageableParser {
    public static Pageable makePageable(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }
        if (from >= 0 && size > 0) {
            return PageRequest.of(from / size, size);
        } else {
            throw new IncorrectRequestException("Parameter from must be >= 0, size > 0");
        }
    }
}
