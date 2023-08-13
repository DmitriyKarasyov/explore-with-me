package ru.practicum.main_service.participation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.event.model.event.Event;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.participation.status.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participation_requests", schema = "public")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "created")
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @OneToOne
    @JoinColumn(name = "requester_id")
    private User requester;
    @Enumerated(EnumType.STRING)
    @Column
    private Status status;
}
