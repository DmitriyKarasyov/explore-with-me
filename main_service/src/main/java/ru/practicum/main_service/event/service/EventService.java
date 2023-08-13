package ru.practicum.main_service.event.service;

import ru.practicum.main_service.event.dto.*;
import ru.practicum.main_service.participation.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventShortDto> getUserEvents(Integer userId, Integer from, Integer size);

    EventFullDto postEvent(Integer userId, NewEventDto newEventDto);

    EventFullDto getEventByIdPrivate(Integer userId, Integer eventId);

    EventFullDto patchEventUser(Integer userId, Integer eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getEventRequests(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult updateRequests(Integer userId,
                                                  Integer eventId,
                                                  EventRequestStatusUpdateRequest request);

    List<EventFullDto> getEventsAdmin(Integer[] users, String[] states, Integer[] categories, String rangeStart,
                                      String rangeEnd, Integer from, Integer size);

    EventFullDto patchEventAdmin(Integer eventId, UpdateEventAdminRequest updateRequest);

    List<EventShortDto> getEventsPublic(String text, Integer[] categories, Boolean paid, String rangeStart,
                                        String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size);

    EventFullDto getEventByIdPublic(Integer id, HttpServletRequest request);
}
