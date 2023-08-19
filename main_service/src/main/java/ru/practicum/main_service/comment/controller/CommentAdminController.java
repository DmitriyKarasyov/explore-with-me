package ru.practicum.main_service.comment.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.comment.dto.BlockCommentDto;
import ru.practicum.main_service.comment.dto.SingleCommentAdminDto;
import ru.practicum.main_service.comment.repository.CommentRepository;
import ru.practicum.main_service.comment.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private CommentRepository commentRepository;
    private final CommentService commentService;

    @Autowired
    public CommentAdminController(CommentRepository commentRepository,
                                  CommentService commentService) {
        this.commentRepository = commentRepository;
        this.commentService = commentService;
    }

    @GetMapping("/suspicious")
    public List<SingleCommentAdminDto> getSuspiciousComments(
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("get suspicious comments");
        return commentService.getSuspiciousComments(from, size);
    }

    @PatchMapping("/block")
    public SingleCommentAdminDto blockComment(@RequestBody @Valid BlockCommentDto blockCommentDto) {
        log.info("block comment with id={}, moderatorId={}",
                blockCommentDto.getCommentId(), blockCommentDto.getModeratorId());
        return commentService.blockComment(blockCommentDto.getCommentId(), blockCommentDto.getModeratorId());
    }
}
