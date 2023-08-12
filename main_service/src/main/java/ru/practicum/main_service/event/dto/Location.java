package ru.practicum.main_service.event.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private Float lat;
    private Float lon;
}
