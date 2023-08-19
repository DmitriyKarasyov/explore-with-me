package ru.practicum.main_service.comment.dto;

import lombok.Data;
import ru.practicum.main_service.validation.NotBlank;

@Data
public class BlockCommentDto {
    @NotBlank(fieldName = "commentId")
    private Integer commentId;
    @NotBlank(fieldName = "moderatorId")
    private Integer moderatorId;
}
