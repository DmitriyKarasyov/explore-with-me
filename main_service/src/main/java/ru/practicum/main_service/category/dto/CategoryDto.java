package ru.practicum.main_service.category.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main_service.validation.NotBlank;

@Data
@Builder
public class CategoryDto {
    private Integer id;
    @NotBlank(
            fieldName = "name"
    )
    private String name;
}
