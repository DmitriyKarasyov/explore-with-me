package ru.practicum.main_service.category.dto;

import lombok.Data;
import ru.practicum.main_service.validation.Length;

@Data
public class NewCategoryDto {
    @Length(
            fieldName = "name",
            min = 1,
            max = 50
    )
    private String name;
}
