package ru.practicum.main_service.event.mapper;

import ru.practicum.main_service.event.model.sort.Sort;
import ru.practicum.main_service.exception.IncorrectRequestException;

public class SortMapper {

    public static Sort makeSort(String sortString) {
        try {
            return Sort.valueOf(sortString);
        } catch (IllegalArgumentException e) {
            throw new IncorrectRequestException(String.format("Unknown user sort: %s", sortString));
        }
    }
}
