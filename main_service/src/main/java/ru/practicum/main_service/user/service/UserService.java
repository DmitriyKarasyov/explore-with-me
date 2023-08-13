package ru.practicum.main_service.user.service;

import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> adminGetUsers(List<Integer> ids, Integer from, Integer size);

    UserDto postUser(NewUserRequest newUserRequest);

    void deleteUser(Integer userId);
}
