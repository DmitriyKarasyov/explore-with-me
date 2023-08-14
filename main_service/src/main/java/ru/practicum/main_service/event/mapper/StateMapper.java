package ru.practicum.main_service.event.mapper;

import ru.practicum.main_service.event.model.state.State;
import ru.practicum.main_service.exception.IncorrectRequestException;

import java.util.ArrayList;
import java.util.List;

public class StateMapper {

    public static State makeState(String stateString) {
        try {
            return State.valueOf(stateString);
        } catch (IllegalArgumentException e) {
            throw new IncorrectRequestException(String.format("Unknown event state: %s", stateString));
        }
    }

    public static List<State> makeState(List<String> stateStrings) {
        List<State> states = new ArrayList<>();
        for (String stateString : stateStrings) {
            states.add(makeState(stateString));
        }
        return states;
    }
}
