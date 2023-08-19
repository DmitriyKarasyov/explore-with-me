package ru.practicum.main_service.comment.service;

import ru.practicum.main_service.comment.dto.*;
import ru.practicum.main_service.comment.model.Comment;

import java.util.List;

public interface CommentService {
    SingleCommentPublicDto addComment(NewCommentDto newCommentDto);

    CommentChainDto loadCommentChain(Integer id);

    Comment editComment(EditCommentDto editCommentDto);

    List<SingleCommentPublicDto> getEventComments(Integer eventId);

    void complainAboutComment(Integer commentId);

    SingleCommentAdminDto blockComment(Integer commentId, Integer moderatorId);

    List<SingleCommentAdminDto> getSuspiciousComments(Integer from, Integer size);
}
