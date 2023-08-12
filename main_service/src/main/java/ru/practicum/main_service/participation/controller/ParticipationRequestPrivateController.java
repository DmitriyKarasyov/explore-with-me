package ru.practicum.main_service.participation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.participation.dto.ParticipationRequestDto;
import ru.practicum.main_service.participation.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
public class ParticipationRequestPrivateController {
    private final ParticipationRequestService service;

    @Autowired
    public ParticipationRequestPrivateController(ParticipationRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Integer userId) {
        log.info("get participation requests made by user with id={}", userId);
        return service.getUserRequests(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequest(@PathVariable Integer userId,
                                               @RequestParam Integer eventId) {
        log.info("post participation request, userId={}, eventId={}", userId, eventId);
        return service.postRequest(userId, eventId);
    }
}
