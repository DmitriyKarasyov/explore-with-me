package ru.practicum.main_service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.validation.NotBlank;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    @NotBlank(fieldName = "requestIds")
    private List<Integer> requestIds;

    @NotBlank(fieldName = "status")
    private String status;
}
