package ru.practicum.main_service.participation.service;

import ru.practicum.main_service.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    List<ParticipationRequestDto> getUserRequests(Integer userId);

    ParticipationRequestDto postRequest(Integer userId, Integer eventId);
}
