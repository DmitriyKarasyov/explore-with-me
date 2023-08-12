package ru.practicum.main_service.compilation.dto;

import lombok.Data;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.NotBlank;

import java.util.Set;

@Data
public class NewCompilationDto {
    private Set<Integer> events;
    private Boolean pined;
    @NotBlank(fieldName = "title")
    @Length(fieldName = "title", min = 1, max = 50)
    private String title;
}
