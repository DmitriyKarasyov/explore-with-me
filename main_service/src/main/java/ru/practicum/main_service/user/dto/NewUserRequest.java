package ru.practicum.main_service.user.dto;

import lombok.Data;
import ru.practicum.main_service.validation.Email;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.NotBlank;

@Data
public class NewUserRequest {
    @NotBlank(
            fieldName = "email"
    )
    @Email
    private String email;
    @NotBlank(
            fieldName = "name"
    )
    @Length(
            fieldName = "name",
            min = 2,
            max = 250
    )
    private String name;
}
