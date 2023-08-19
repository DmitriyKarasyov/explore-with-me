package ru.practicum.main_service.comment.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.comment.dto.*;
import ru.practicum.main_service.comment.mapper.CommentMapper;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.comment.service.CommentService;
import ru.practicum.main_service.exception.IncorrectRequestException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @Autowired
    public CommentPublicController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SingleCommentPublicDto addComment(@RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("post new comment: {}", newCommentDto);
        if (newCommentDto.getEventId() == null && newCommentDto.getReplyToId() == null) {
            throw new IncorrectRequestException("both eventId and replyToId cannot be null");
        }
        if (newCommentDto.getEventId() != null && newCommentDto.getReplyToId() != null) {
            throw new IncorrectRequestException("either eventId or replyToId should be specified, not both");
        }

        return commentService.addComment(newCommentDto);
    }

    @GetMapping("chain/{commentId}")
    public CommentChainDto loadCommentChain(@PathVariable Integer commentId) {
        log.info("get chain od comment with id={}", commentId);
        return commentService.loadCommentChain(commentId);
    }

    @PatchMapping
    public SingleCommentPublicDto patchComment(@RequestBody @Valid EditCommentDto editCommentDto) {
        log.info("edit comment, editCommentDto: {}", editCommentDto);
        Comment comment = commentService.editComment(editCommentDto);
        return CommentMapper.makeSingleCommentPublicDto(comment);
    }

    @GetMapping("/{eventId}")
    public List<SingleCommentPublicDto> getEventComments(@PathVariable Integer eventId) {
        log.info("get event comments, event id={}", eventId);
        return commentService.getEventComments(eventId);
    }

    @PatchMapping("/complain")
    public void complain(@RequestBody ComplainCommentDto complainCommentDto) {
        commentService.complainAboutComment(complainCommentDto.getCommentId());
    }
}
