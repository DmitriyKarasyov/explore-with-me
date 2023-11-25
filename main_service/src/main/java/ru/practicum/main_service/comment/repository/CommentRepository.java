package ru.practicum.main_service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.comment.model.CommentState;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByEvent_IdOrderByPostedAt(Integer eventId);

    List<Comment> findByState(CommentState state, Pageable pageable);
}
