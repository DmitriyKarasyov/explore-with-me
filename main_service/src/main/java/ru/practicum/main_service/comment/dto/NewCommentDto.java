package ru.practicum.main_service.comment.dto;

import lombok.Data;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.NotBlank;

@Data
public class NewCommentDto {
    @NotBlank(fieldName = "text")
    @Length(fieldName = "text", min = 1, max = 7000)
    private String text;
    private Integer eventId;
    @NotBlank(fieldName = "authorId")
    private Integer authorId;
    private Integer replyToId;
}
