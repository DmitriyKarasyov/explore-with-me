package ru.practicum.main_service.comment.dto;

import lombok.Data;
import ru.practicum.main_service.validation.NotBlank;

@Data
public class EditCommentDto {
    @NotBlank(fieldName = "id")
    private Integer id;
    @NotBlank(fieldName = "newText")
    private String text;
}
