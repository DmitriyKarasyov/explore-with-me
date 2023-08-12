package ru.practicum.main_service.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @Length(
            fieldName = "name",
            min = 1,
            max = 50
    )
    @NotBlank(fieldName = "name")
    private String name;
}
