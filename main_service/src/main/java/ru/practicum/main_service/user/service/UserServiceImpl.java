package ru.practicum.main_service.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.user.mapper.UserMapper;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.common.DBRequest;
import ru.practicum.main_service.common.PageableParser;
import ru.practicum.main_service.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final DBRequest<User> dbRequest;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
        this.dbRequest = new DBRequest<>(repository);
    }

    @Override
    @Transactional
    public List<UserDto> adminGetUsers(List<Integer> ids, Integer from, Integer size) {
        Pageable pageable = PageableParser.makePageable(from, size);
            if (ids.isEmpty()) {
                List<User> allUsers = repository.findAll(pageable).toList();
                return UserMapper.makeUserDto(allUsers);
            }
            if (!repository.existsByIdIn(ids)) {
                return new ArrayList<>();
            }
            List<User> foundUsers = repository.findAllByIdIn(ids, pageable);
            return UserMapper.makeUserDto(foundUsers);
    }

    @Override
    @Transactional
    public UserDto postUser(NewUserRequest newUserRequest) {
        User savedUser = dbRequest.tryRequest(repository::save, UserMapper.makeUser(newUserRequest));
        return UserMapper.makeUserDto(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId) {
        dbRequest.checkExistence(User.class, userId);
        repository.deleteById(userId);
    }
}
