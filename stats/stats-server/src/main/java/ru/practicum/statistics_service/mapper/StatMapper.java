package ru.practicum.statistics_service.mapper;

import org.springframework.stereotype.Service;
import ru.practicum.statistics_service.dto.EndpointHitDto;
import ru.practicum.statistics_service.dto.ViewStatsDto;
import ru.practicum.statistics_service.model.EndpointHit;
import ru.practicum.statistics_service.model.ViewStats;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatMapper {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EndpointHit makeEndpointHit(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .build();
    }

    public static ViewStatsDto makeViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }

    public static List<ViewStatsDto> makeViewStatsDto(List<ViewStats> viewStatsList) {
        List<ViewStatsDto> viewStatsDtoList = new ArrayList<>();
        for (ViewStats viewStats : viewStatsList) {
            viewStatsDtoList.add(makeViewStatsDto(viewStats));
        }
        return viewStatsDtoList;
    }
}
