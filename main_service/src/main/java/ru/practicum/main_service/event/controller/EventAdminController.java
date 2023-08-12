package ru.practicum.main_service.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.UpdateEventAdminRequest;
import ru.practicum.main_service.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
public class EventAdminController {
    private final EventService service;

    @Autowired
    public EventAdminController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) Integer[] users,
                                        @RequestParam(required = false) String[] states,
                                        @RequestParam(required = false) Integer[] categories,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) String rangeEnd,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("get events, users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return service.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventAdmin(@PathVariable Integer eventId,
                                        @RequestBody UpdateEventAdminRequest updateRequest) {
        log.info("admin update event with id={}, update request: {}", eventId, updateRequest);
        return service.patchEventAdmin(eventId, updateRequest);
    }
}
