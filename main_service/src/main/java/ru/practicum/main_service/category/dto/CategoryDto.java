package ru.practicum.main_service.category.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.NotBlank;

@Data
@Builder
public class CategoryDto {
    private Integer id;
    @NotBlank(
            fieldName = "name"
    )
    @Length(fieldName = "name", min = 1, max = 50)
    private String name;
}
