package ru.practicum.main_service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.NotBlank;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private Set<Integer> events;
    private Boolean pined;
    @NotBlank(fieldName = "title")
    @Length(fieldName = "title", min = 1, max = 50)
    private String title;
}
