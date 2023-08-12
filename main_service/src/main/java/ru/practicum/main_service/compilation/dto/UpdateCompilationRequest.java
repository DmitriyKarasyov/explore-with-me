package ru.practicum.main_service.compilation.dto;

import lombok.Data;
import ru.practicum.main_service.validation.Length;

import java.util.Set;

@Data
public class UpdateCompilationRequest {
    private Set<Integer> events;
    private Boolean pinned;
    @Length(fieldName = "title", min = 1, max = 50)
    private String title;
}
