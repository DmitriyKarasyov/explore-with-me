package ru.practicum.main_service.compilation.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.event.model.event.Event;

import javax.persistence.*;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "compilations", schema = "public")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "compilations_events", schema = "public",
            joinColumns = {@JoinColumn(name = "id")}, inverseJoinColumns = {@JoinColumn(name = "event_id")})
    private Set<Event> events;
    @Column(name = "pinned")
    private Boolean pinned;
    @Column(name = "title")
    private String title;
}
