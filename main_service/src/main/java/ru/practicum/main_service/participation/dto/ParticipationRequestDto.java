package ru.practicum.main_service.participation.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipationRequestDto {
    private Integer id;
    private String created;
    private Integer event;
    private Integer requester;
    private String status;
}
