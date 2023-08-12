package ru.practicum.main_service.event.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateEventUserRequest extends UpdateEventRequest {
    private String stateAction;
}
