package ru.practicum.main_service.comment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.comment.dto.*;
import ru.practicum.main_service.comment.mapper.CommentMapper;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.comment.model.CommentState;
import ru.practicum.main_service.comment.repository.CommentRepository;
import ru.practicum.main_service.common.DBRequest;
import ru.practicum.main_service.common.PageableParser;
import ru.practicum.main_service.event.mapper.EventMapper;
import ru.practicum.main_service.event.model.event.Event;
import ru.practicum.main_service.event.repository.EventRepository;
import ru.practicum.main_service.exception.ConditionViolationException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {

    private static final int HOURS_TO_EDIT_COMMENT = 12;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final DBRequest<Comment> commentDBRequest;
    private final DBRequest<User> userDBRequest;
    private final DBRequest<Event> eventDBRequest;


    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        commentDBRequest = new DBRequest<>(commentRepository);
        userDBRequest = new DBRequest<>(userRepository);
        eventDBRequest = new DBRequest<>(eventRepository);
    }

    @Override
    @Transactional
    public SingleCommentPublicDto addComment(NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setCommentText(newCommentDto.getText());
        if (newCommentDto.getEventId() != null) {
            eventDBRequest.checkExistence(Event.class, newCommentDto.getEventId());
            comment.setEvent(eventRepository.getReferenceById(newCommentDto.getEventId()));
        }

        userDBRequest.checkExistence(User.class, newCommentDto.getAuthorId());
        comment.setAuthor(userRepository.getReferenceById(newCommentDto.getAuthorId()));

        if (newCommentDto.getReplyToId() != null) {
            commentDBRequest.checkExistence(Comment.class, newCommentDto.getReplyToId());
            comment.setReplyTo(commentRepository.getReferenceById(newCommentDto.getReplyToId()));
        }

        comment.setPostedAt(LocalDateTime.now());
        comment.setState(CommentState.NEW);

        return CommentMapper.makeSingleCommentPublicDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentChainDto loadCommentChain(Integer id) {
        commentDBRequest.checkExistence(Comment.class, id);
        Comment comment = commentRepository.getReferenceById(id);
        Map<Integer, CommentChainDto> idToDto = new HashMap<>();
        Comment current = comment;
        idToDto.put(current.getId(), CommentMapper.makeCommentChainDto(comment));
        while (current.getReplyTo() != null) {
            Comment prev = current;
            current = current.getReplyTo();
            idToDto.put(current.getId(), CommentMapper.makeCommentChainDto(current));
            idToDto.get(prev.getId()).setParent(idToDto.get(current.getId()));
            idToDto.get(current.getId()).setReplies(null);
        }
        idToDto.get(current.getId()).setEvent(EventMapper.makeEventShortDto(current.getEvent()));
        Queue<Comment> comments = new LinkedList<>();
        addChildrenToQueue(comment, comments);
        while (!comments.isEmpty()) {
            current = comments.poll();
            idToDto.put(current.getId(), CommentMapper.makeCommentChainDto(current));
            idToDto.get(current.getReplyTo().getId()).getReplies().add(idToDto.get(current.getId()));
            addChildrenToQueue(current, comments);
        }
        return idToDto.get(id);
    }

    @Override
    @Transactional
    public List<SingleCommentAdminDto> getSuspiciousComments(Integer from, Integer size) {
        Pageable pageable = PageableParser.makePageable(from, size);
        List<Comment> comments = commentRepository.findByState(CommentState.SUSPICIOUS, pageable);
        return CommentMapper.makeSingleCommentAdminDto(comments);
    }

    private void addChildrenToQueue(Comment comment, Queue<Comment> queue) {
        comment.getReplies().stream().sorted(Comparator.comparing(Comment::getPostedAt)).forEach(queue::add);
    }

    @Override
    @Transactional
    public Comment editComment(EditCommentDto editCommentDto) {
        commentDBRequest.checkExistence(Comment.class, editCommentDto.getId());
        Comment comment = commentRepository.getReferenceById(editCommentDto.getId());
        if (comment.getPostedAt().isBefore(LocalDateTime.now().minusHours(HOURS_TO_EDIT_COMMENT))) {
            throw new ConditionViolationException("comment is older than 12 hours");
        }
        comment.setCommentText(editCommentDto.getText());
        return commentRepository.save(comment);
    }

    @Override
    public List<SingleCommentPublicDto> getEventComments(Integer eventId) {
        eventDBRequest.checkExistence(Event.class, eventId);
        return CommentMapper.makeSingleCommentPublicDto(commentRepository.findByEvent_IdOrderByPostedAt(eventId));
    }

    @Override
    @Transactional
    public void complainAboutComment(Integer commentId) {
        commentDBRequest.checkExistence(Comment.class, commentId);
        Comment comment = commentRepository.getReferenceById(commentId);
        if (comment.getState() == CommentState.NEW) {
            comment.setState(CommentState.SUSPICIOUS);
            commentRepository.save(comment);
        }
    }

    @Override
    @Transactional
    public SingleCommentAdminDto blockComment(Integer commentId, Integer moderatorId) {
        commentDBRequest.checkExistence(Comment.class, commentId);
        userDBRequest.checkExistence(User.class, moderatorId);
        Comment comment = commentRepository.getReferenceById(commentId);
        User moderator = userRepository.getReferenceById(moderatorId);
        if (comment.getState() == CommentState.SUSPICIOUS) {
            comment.setState(CommentState.BLOCKED_BY_MODERATOR);
            comment.setModeratedBy(moderator);
            return CommentMapper.makeSingleCommentAdminDto(
                    commentDBRequest.tryRequest(commentRepository::save, comment));
        } else {
            throw new ConditionViolationException("comment state must be SUSPICIOUS");
        }
    }
}
