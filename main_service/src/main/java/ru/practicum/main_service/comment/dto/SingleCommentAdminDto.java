package ru.practicum.main_service.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.comment.model.CommentState;
import ru.practicum.main_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleCommentAdminDto {
    private Integer id;
    private String text;
    private LocalDateTime postedAt;
    private UserShortDto author;
    private CommentState state;
    private UserShortDto moderatedBy;
    private Integer eventId;
}
