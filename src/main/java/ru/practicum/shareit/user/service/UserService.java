package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto add(User user);

    UserDto update(User user);

    void delete(long userId);

    UserDto getById(long userId);
}