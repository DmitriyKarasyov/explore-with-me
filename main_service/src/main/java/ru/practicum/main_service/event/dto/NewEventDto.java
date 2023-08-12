package ru.practicum.main_service.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.event.location.model.Location;
import ru.practicum.main_service.validation.Length;
import ru.practicum.main_service.validation.NotBlank;
import ru.practicum.main_service.validation.ValidEventDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotBlank(fieldName = "annotation")
    @Length(fieldName = "annotation", min = 20, max = 2000)
    private String annotation;

    @NotBlank(fieldName = "category")
    private Integer category;

    @NotBlank(fieldName = "description")
    @Length(fieldName = "description", min = 20, max = 7000)
    private String description;

    @NotBlank(fieldName = "eventDate")
    @ValidEventDate
    private String eventDate;

    @NotBlank(fieldName = "location")
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(fieldName = "title")
    @Length(fieldName = "title", min = 3, max = 120)
    private String title;
}
