package ru.practicum.main_service.comment.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import ru.practicum.main_service.event.model.event.Event;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.validation.Length;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments", schema = "public")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comment_text", nullable = false)
    @Length(fieldName = "commentText", min = 1, max = 7000)
    private String commentText;

    @ManyToOne(optional = true)
    @JoinColumn(name = "event_id", nullable = true)
    private Event event;

    @Column(name = "posted_at", nullable = false)
    private LocalDateTime postedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private CommentState state;

    @ManyToOne(optional = true)
    @JoinColumn(name = "moderated_by_id", nullable = true)
    private User moderatedBy;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id", nullable = true)
    private Comment replyTo;

    @OneToMany(mappedBy = "replyTo", orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Comment> replies;
}
