package ru.practicum.main_service.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.validation.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @Length(
            fieldName = "name",
            min = 1,
            max = 50
    )
    private String name;
}
