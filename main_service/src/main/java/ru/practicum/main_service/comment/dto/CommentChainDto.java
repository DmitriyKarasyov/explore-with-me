package ru.practicum.main_service.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentChainDto {
    private Integer id;
    private String text;
    private LocalDateTime postedAt;
    private UserShortDto author;
    private EventShortDto event;
    private CommentChainDto parent;
    private List<CommentChainDto> replies = new ArrayList<>();
}
