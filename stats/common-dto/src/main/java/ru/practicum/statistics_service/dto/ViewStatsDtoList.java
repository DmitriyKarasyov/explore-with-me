package ru.practicum.statistics_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ViewStatsDtoList {
    private final List<ViewStatsDto> viewStats;
}
