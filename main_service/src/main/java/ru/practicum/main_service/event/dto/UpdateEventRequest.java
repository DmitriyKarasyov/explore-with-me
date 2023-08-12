package ru.practicum.main_service.event.dto;

import lombok.Data;
import ru.practicum.main_service.event.location.model.Location;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.ValidEventDate;

@Data
public class UpdateEventRequest {
    @Length(fieldName = "annotation", min = 20, max = 2000)
    private String annotation;
    private Integer category;
    @Length(fieldName = "description", min = 20, max = 7000)
    private String description;
    @ValidEventDate
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @Length(fieldName = "title", min = 3, max = 120)
    private String title;
}
