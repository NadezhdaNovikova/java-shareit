package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistException;
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
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto add(User user) {
        userValidateAlreadyExistsEmail(user);
        return UserMapper.toUserDto(userStorage.add(user));
    }

    @Override
    public UserDto update(User user) {
        getById(user.getId());
        userValidateAlreadyExistsEmail(user);
        return UserMapper.toUserDto(userStorage.update(user));
    }

    @Override
    public void delete(long userId) {
        userStorage.delete(userId);
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId))));
    }

    private void userValidateAlreadyExistsEmail(User user) {
        List<User> users = userStorage.getAll();
        for (User u : users) {
            Long uId = u.getId();
            if (!uId.equals(user.getId())) {
                if (u.getEmail().equals(user.getEmail())) {
                    log.info("Email принадлежит пользователю " + u);
                    throw new UserAlreadyExistException(String.format("Пользователь с email %s уже существует",
                            user.getEmail()));
                }
            }
        }
    }
}