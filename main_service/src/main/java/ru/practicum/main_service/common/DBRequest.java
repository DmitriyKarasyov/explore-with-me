package ru.practicum.main_service.common;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_service.exception.EWMConstraintViolationException;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.List;

@Data
public class DBRequest<T> {
    private final JpaRepository<T, Integer> repository;

    public DBRequest(JpaRepository<T, Integer> repository) {
        this.repository = repository;
    }

    public T tryRequest(EWMExecutable<T> executable, T parameter) {
        try {
            return executable.execute(parameter);
        } catch (Exception e) {
            throw new EWMConstraintViolationException(e.getMessage());
        }
    }

    public List<T> tryListRequest(EWMListExecutable<T> executable, List<T> parameter) {
        try {
            return executable.execute(parameter);
        } catch (Exception e) {
            throw new EWMConstraintViolationException(e.getMessage());
        }
    }

    public void checkExistence(Class<T> objectClass, Integer id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(String.format("%s with id = %d was not found.", objectClass.getName(), id));
        }
    }
}
