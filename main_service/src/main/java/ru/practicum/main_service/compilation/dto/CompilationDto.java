package ru.practicum.main_service.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main_service.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
public class CompilationDto {
    private Integer id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
