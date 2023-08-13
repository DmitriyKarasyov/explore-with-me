package ru.practicum.main_service.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.participation.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService service;

    @Autowired
    public EventPrivateController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventShortDto> getUserEvents(@PathVariable Integer userId,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("get events, created by user with id={}", userId);
        return service.getUserEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@PathVariable Integer userId,
                                  @RequestBody @Valid NewEventDto newEventDto) {
        log.info("post new event: {}", newEventDto);
        return service.postEvent(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Integer userId,
                                         @PathVariable Integer eventId) {
        log.info("get event with id={}, initiated by user with id={}", eventId, userId);
        return service.getEventByIdPrivate(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventUser(@PathVariable Integer userId,
                                       @PathVariable Integer eventId,
                                       @RequestBody @Valid UpdateEventUserRequest updateRequest) {
        log.info("patch event with id={}, initiated by user with id={}, update event: {}", eventId, userId,
                updateRequest);
        return service.patchEventUser(userId, eventId, updateRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Integer userId,
                                                          @PathVariable Integer eventId) {
        log.info("get requests of event with id={}, initiated by user with id={}", eventId, userId);
        return service.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Integer userId,
                                                         @PathVariable Integer eventId,
                                                         @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        log.info("request to update status of event requests, event id={}, event initiator id={}, ids of requests to " +
                "update: {}", eventId, userId, request);
        return service.updateRequests(userId, eventId, request);
    }
}
