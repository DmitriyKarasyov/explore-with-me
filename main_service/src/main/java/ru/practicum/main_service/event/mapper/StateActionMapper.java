package ru.practicum.main_service.event.mapper;

import ru.practicum.main_service.event.model.state_action.AdminStateAction;
import ru.practicum.main_service.event.model.state_action.UserStateAction;
import ru.practicum.main_service.exception.IncorrectRequestException;

public class StateActionMapper {

    public static UserStateAction makeUserStateAction(String stateString) {
        try {
            return UserStateAction.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new IncorrectRequestException(String.format("Unknown user state action: %s", stateString));
        }
    }

    public static AdminStateAction makeAdminStateAction(String stateString) {
        try {
            return AdminStateAction.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new IncorrectRequestException(String.format("Unknown admin state action: %s", stateString));
        }
    }
}
