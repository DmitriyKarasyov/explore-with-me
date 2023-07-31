package ru.practicum.statistics_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.statistics_service.model.EndpointHit;

public interface StatRepository extends JpaRepository<EndpointHit, Integer>,
        QuerydslPredicateExecutor<EndpointHit> {
}
