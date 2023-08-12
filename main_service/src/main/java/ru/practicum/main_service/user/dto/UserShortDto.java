package ru.practicum.main_service.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserShortDto {
    private Integer id;
    private String name;
}
