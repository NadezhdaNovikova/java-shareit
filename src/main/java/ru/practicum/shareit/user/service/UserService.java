package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto add(UserDto userDto);

    UserDto update(UserDto userDto);

    void delete(long userId);

    UserDto getById(long userId);
}