package ru.practicum.statistics_service.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
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

@Service
public class StatService {
    private final StatRepository statRepository;
    private final EntityManager entityManager;

    @Autowired
    public StatService(StatRepository statRepository, JpaContext jpaContext) {
        this.statRepository = statRepository;
        this.entityManager = jpaContext.getEntityManagerByManagedType(EndpointHit.class);
    }

    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        statRepository.save(StatMapper.makeEndpointHit(endpointHitDto));
    }

    @Transactional
    public List<ViewStatsDto> getStatistics(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startDate = LocalDateTime.parse(start, StatMapper.formatter);
        LocalDateTime endDate = LocalDateTime.parse(end, StatMapper.formatter);
        BooleanBuilder whereClause = new BooleanBuilder();
        QEndpointHit endpointHit = QEndpointHit.endpointHit;
        whereClause.and(endpointHit.timestamp.after(startDate)).and(endpointHit.timestamp.before(endDate));

        if (uris != null && !uris.isEmpty()) {
            whereClause.and(endpointHit.uri.in(uris));
        }

        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);
        List<Tuple> viewStatsTuple;

        if (unique != null) {
            viewStatsTuple = query.select(endpointHit.app, endpointHit.uri, endpointHit.ip.countDistinct())
                    .from(endpointHit).where(whereClause).groupBy(endpointHit.app, endpointHit.uri).fetch();
        } else {
            viewStatsTuple = query.select(endpointHit.app, endpointHit.uri, endpointHit.ip.count())
                    .from(endpointHit).where(whereClause).groupBy(endpointHit.app, endpointHit.uri).fetch();
        }

        return StatMapper.makeViewStatsDto(makeViewStats(viewStatsTuple));
    }

    public List<ViewStats> makeViewStats(List<Tuple> viewStatsTuple) {
        List<ViewStats> viewStatsList = new ArrayList<>();
        for (Tuple tuple : viewStatsTuple) {
            viewStatsList.add(ViewStats.builder()
                    .app(tuple.get(0, String.class))
                    .uri(tuple.get(1, String.class))
                    .hits(tuple.get(2, Long.class))
                    .build());
        }
        return viewStatsList;
    }
}
