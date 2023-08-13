package ru.practicum.main_service.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventPublicController {
    private final EventService service;

    @Autowired
    public EventPublicController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) Integer[] categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false) Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @RequestParam(required = false, defaultValue = "10") Integer size,
                                         HttpServletRequest request) {
        log.info("public get events, text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, " +
                        "sort={}, from={}, size={}", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size);
        return service.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size,
                request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Integer id, HttpServletRequest request) {
        log.info("public get event with id={}", id);
        return service.getEventByIdPublic(id, request);
    }
}
