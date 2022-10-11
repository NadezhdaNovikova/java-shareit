package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public List<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto add(UserDto user) {
        return UserMapper.toUserDto(userStorage.save(UserMapper.toUser(user)));
    }

    @Transactional
    @Override
    public UserDto update(UserDto user) {
        User oldUser = UserMapper.toUser(getById(user.getId()));
        if (user.getName() != null && !(user.getName().trim().isBlank())) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !(user.getEmail().trim().isBlank())) {
            oldUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userStorage.save(oldUser));
    }

    @Transactional
    @Override
    public void delete(long userId) {
        userStorage.deleteById(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId))));
    }
}