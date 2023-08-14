package ru.practicum.main_service.event.mapper;

import ru.practicum.main_service.event.model.status.UpdateRequestStatus;
import ru.practicum.main_service.exception.IncorrectRequestException;

public class StatusMapper {

    public static UpdateRequestStatus makeUpdateRequestStatus(String updateRequestStatusString) {
        try {
            return UpdateRequestStatus.valueOf(updateRequestStatusString);
        } catch (IllegalArgumentException e) {
            throw new IncorrectRequestException(
                    String.format("Unknown update request status: %s", updateRequestStatusString)
            );
        }
    }
}
