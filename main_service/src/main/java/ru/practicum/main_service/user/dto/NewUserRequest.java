package ru.practicum.main_service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.validation.Email;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
