package ru.practicum.main_service.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.user.dto.UserShortDto;

@Data
@Builder
public class EventShortDto {
    private Integer id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
