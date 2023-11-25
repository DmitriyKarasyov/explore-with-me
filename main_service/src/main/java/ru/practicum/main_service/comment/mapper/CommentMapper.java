package ru.practicum.main_service.comment.mapper;

import ru.practicum.main_service.comment.dto.CommentChainDto;
import ru.practicum.main_service.comment.dto.SingleCommentAdminDto;
import ru.practicum.main_service.comment.dto.SingleCommentPublicDto;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static SingleCommentPublicDto makeSingleCommentPublicDto(Comment comment) {
        return SingleCommentPublicDto.builder()
                .id(comment.getId())
                .author(UserMapper.makeUserShortDto(comment.getAuthor()))
                .postedAt(comment.getPostedAt())
                .text(comment.getCommentText())
                .build();
    }

    public static List<SingleCommentPublicDto> makeSingleCommentPublicDto(List<Comment> comments) {
        List<SingleCommentPublicDto> commentPublicDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentPublicDtoList.add(makeSingleCommentPublicDto(comment));
        }
        return commentPublicDtoList;
    }

    public static CommentChainDto makeCommentChainDto(Comment comment) {
        return CommentChainDto.builder()
                .id(comment.getId())
                .author(UserMapper.makeUserShortDto(comment.getAuthor()))
                .postedAt(comment.getPostedAt())
                .text(comment.getCommentText())
                .replies(new ArrayList<>())
                .build();
    }

    public static SingleCommentAdminDto makeSingleCommentAdminDto(Comment comment) {
        return SingleCommentAdminDto.builder()
                .id(comment.getId())
                .author(UserMapper.makeUserShortDto(comment.getAuthor()))
                .postedAt(comment.getPostedAt())
                .text(comment.getCommentText())
                .state(comment.getState())
                .eventId(comment.getEvent().getId())
                .moderatedBy(UserMapper.makeUserShortDto(comment.getModeratedBy()))
                .build();
    }

    public static List<SingleCommentAdminDto> makeSingleCommentAdminDto(List<Comment> comments) {
        List<SingleCommentAdminDto> commentAdminDtoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentAdminDtoList.add(makeSingleCommentAdminDto(comment));
        }
        return commentAdminDtoList;
    }
}
