package ru.practicum.main_service.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.user.dto.UserShortDto;

@Data
@Builder
public class EventFullDto {
    private Integer id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Integer views;
}
