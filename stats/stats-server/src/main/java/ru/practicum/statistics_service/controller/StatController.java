package ru.practicum.statistics_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistics_service.dto.EndpointHitDto;
import ru.practicum.statistics_service.service.StatService;

import java.util.List;

@Slf4j
@RestController
public class StatController {
    private final StatService statService;

    @Autowired
    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Save hit = {}", endpointHitDto);
        statService.saveHit(endpointHitDto);
    }

    @GetMapping("/stats")
    public String getStats(@RequestParam(required = false) String start,
                                            @RequestParam(required = false) String end,
                                            @RequestParam(required = false) List<String> uris,
                                            @RequestParam(required = false) Boolean unique) {
        return statService.getStatistics(start, end, uris, unique);
    }
}
