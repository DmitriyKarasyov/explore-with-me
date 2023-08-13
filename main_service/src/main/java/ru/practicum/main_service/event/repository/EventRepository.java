package ru.practicum.main_service.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.main_service.event.model.event.Event;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Integer>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiator_Id(Integer initiatorId, Pageable pageable);

    Boolean existsAllByIdIn(Set<Integer> ids);

    List<Event> findAllByIdIn(Set<Integer> ids);
}
