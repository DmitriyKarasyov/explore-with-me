package ru.practicum.main_service.common;

import java.util.List;

public interface EWMListExecutable<T> {

    List<T> execute(List<T> types);
}
