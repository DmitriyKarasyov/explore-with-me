package ru.practicum.main_service.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/users")
public class UserAdminController {
    private final UserService service;

    @Autowired
    public UserAdminController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Integer[] ids,
                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<Integer> idsList = new ArrayList<>();
        if (ids != null) {
            idsList = List.of(ids);
        }
        log.info("get users, ids={}, from={}, size={}", idsList, from, size);
        return service.adminGetUsers(idsList, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("post user: {}", newUserRequest);
        return service.postUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer userId) {
        log.info("delete user with id={}", userId);
        service.deleteUser(userId);
    }
}
