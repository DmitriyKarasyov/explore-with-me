package ru.practicum.main_service.event.mapper;

import ru.practicum.main_service.category.mapper.CategoryMapper;
import ru.practicum.main_service.common.EWMDateFormatter;
import ru.practicum.main_service.event.dto.EventFullDto;
import ru.practicum.main_service.event.dto.Location;
import ru.practicum.main_service.event.model.event.Event;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class EventMapper {

    public static EventShortDto makeEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.makeCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(EWMDateFormatter.FORMATTER))
                .initiator(UserMapper.makeUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static List<EventShortDto> makeEventShortDto(List<Event> eventList) {
        List<EventShortDto> eventShortDtoList = new ArrayList<>();
        for (Event event : eventList) {
            eventShortDtoList.add(makeEventShortDto(event));
        }
        return eventShortDtoList;
    }

    public static EventFullDto makeEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.makeCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(EWMDateFormatter.FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(EWMDateFormatter.FORMATTER))
                .initiator(UserMapper.makeUserShortDto(event.getInitiator()))
                .location(Location.builder()
                        .lat(event.getLat())
                        .lon(event.getLon())
                        .build())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() == null ? null
                        : event.getPublishedOn().format(EWMDateFormatter.FORMATTER))
                .requestModeration(event.getRequestModeration())
                .state(event.getState() == null ? null : event.getState().toString())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static List<EventFullDto> makeEventFullDto(List<Event> events) {
        List<EventFullDto> eventFullDtoList = new ArrayList<>();
        for (Event event : events) {
            eventFullDtoList.add(makeEventFullDto(event));
        }
        return eventFullDtoList;
    }
}
