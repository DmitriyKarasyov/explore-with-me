package ru.practicum.main_service.event.dto;

import lombok.Data;
import ru.practicum.main_service.validation.NotBlank;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    @NotBlank(fieldName = "requestIds")
    private List<Integer> requestsIds;

    @NotBlank(fieldName = "status")
    private String status;
}
