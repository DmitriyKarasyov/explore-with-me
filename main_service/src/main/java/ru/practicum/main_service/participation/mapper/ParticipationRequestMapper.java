package ru.practicum.main_service.participation.mapper;

import ru.practicum.main_service.common.EWMDateFormatter;
import ru.practicum.main_service.participation.dto.ParticipationRequestDto;
import ru.practicum.main_service.participation.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

public class ParticipationRequestMapper {

    public static ParticipationRequestDto makeParticipationRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated().format(EWMDateFormatter.FORMATTER))
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().toString())
                .build();
    }

    public static List<ParticipationRequestDto> makeParticipationRequestDto(List<ParticipationRequest> requests) {
        List<ParticipationRequestDto> requestDtoList = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            requestDtoList.add(makeParticipationRequestDto(request));
        }
        return requestDtoList;
    }
}
