package ru.practicum.statistics_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistics_service.dto.EndpointHitDto;
import ru.practicum.statistics_service.dto.ViewStatsDto;
import ru.practicum.statistics_service.service.StatService;

import java.util.List;

@RestController
@Slf4j
public class StatController {
    private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    public void saveHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Save hit = {}", endpointHitDto);
        statService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam(required = false) String start,
                                            @RequestParam(required = false) String end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(required = false) Boolean unique) {
        log.info("get statistics from start: {}, end: {}, uris: {}, unique: {}", start, end, uris, unique);
        return statService.getStatistics(start, end, uris, unique);
    }
}
