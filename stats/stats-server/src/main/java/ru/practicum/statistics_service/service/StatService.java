package ru.practicum.statistics_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statistics_service.dto.EndpointHitDto;
import ru.practicum.statistics_service.dto.ViewStatsDto;
import ru.practicum.statistics_service.mapper.StatMapper;
import ru.practicum.statistics_service.model.EndpointHit;
import ru.practicum.statistics_service.model.QEndpointHit;
import ru.practicum.statistics_service.model.ViewStats;
import ru.practicum.statistics_service.repository.StatRepository;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StatService {
    private final StatRepository statRepository;
    private final EntityManager entityManager;
    private final ObjectMapper mapper;

    @Autowired
    public StatService(StatRepository statRepository, JpaContext jpaContext) {
        this.statRepository = statRepository;
        this.entityManager = jpaContext.getEntityManagerByManagedType(EndpointHit.class);
        mapper = new ObjectMapper();
    }

    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        statRepository.save(StatMapper.makeEndpointHit(endpointHitDto));
    }

    @Transactional
    public String getStatistics(String start, String end, List<String> uris, Boolean unique) {
        log.info("stat service receive request to get stats, start={}, end={}, uris={}, unique={}", start, end,
                uris, unique);
        BooleanBuilder whereClause = new BooleanBuilder();
        QEndpointHit endpointHit = QEndpointHit.endpointHit;
        if (start != null && !start.isBlank()) {
            LocalDateTime startDate = LocalDateTime.parse(start, StatMapper.formatter);
            whereClause.and(endpointHit.timestamp.after(startDate));
        }
        if (end != null && !end.isBlank()) {
            LocalDateTime endDate = LocalDateTime.parse(end, StatMapper.formatter);
            whereClause.and(endpointHit.timestamp.before(endDate));
        }
        if (uris != null && !uris.isEmpty()) {
            whereClause.and(endpointHit.uri.contains(uris.iterator().next()));
        }

        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
        List<Tuple> viewStatsTuple;
        NumberPath<Integer> aliasQuantity = Expressions.numberPath(Integer.class, "quantity");

        if (unique != null) {
            viewStatsTuple = query.select(endpointHit.app, endpointHit.uri, endpointHit.ip.countDistinct().intValue().as(aliasQuantity))
                    .from(endpointHit).where(whereClause).groupBy(endpointHit.app, endpointHit.uri)
                    .orderBy(aliasQuantity.desc()).fetch();
        } else {
            viewStatsTuple = query.select(endpointHit.app, endpointHit.uri, endpointHit.ip.count().intValue().as(aliasQuantity))
                    .from(endpointHit).where(whereClause).groupBy(endpointHit.app, endpointHit.uri)
                    .orderBy(aliasQuantity.desc()).fetch();
        }
        List<ViewStatsDto> viewStatsDtoList = StatMapper.makeViewStatsDto(makeViewStats(viewStatsTuple));
        try {
            log.info("returning: view stats list={}", mapper.writeValueAsString(viewStatsDtoList));
            return mapper.writeValueAsString(viewStatsDtoList);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<ViewStats> makeViewStats(List<Tuple> viewStatsTuple) {
        List<ViewStats> viewStatsList = new ArrayList<>();
        for (Tuple tuple : viewStatsTuple) {
            viewStatsList.add(ViewStats.builder()
                    .app(tuple.get(0, String.class))
                    .uri(tuple.get(1, String.class))
                    .hits(tuple.get(2, Integer.class))
                    .build());
        }
        return viewStatsList;
    }
}
