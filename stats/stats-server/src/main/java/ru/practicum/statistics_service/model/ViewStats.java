package ru.practicum.statistics_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ViewStats {
    private String app;
    private String uri;
    private long hits;
}
