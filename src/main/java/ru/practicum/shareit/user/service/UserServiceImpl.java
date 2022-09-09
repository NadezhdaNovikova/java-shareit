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
        User oldUser = userStorage.getById(user.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", user.getId())));
        userValidateAlreadyExistsEmail(user);
        if (user.getName() != null && !(user.getName().trim().isBlank())) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !(user.getEmail().trim().isBlank())) {
            userStorage.deleteEmailUnique(oldUser.getEmail());
            oldUser.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(userStorage.update(oldUser));
    }

    @Override
    public void delete(long userId) {
        String email = getById(userId).getEmail();
        userStorage.delete(userId);
        userStorage.deleteEmailUnique(email);
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(userStorage.getById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId))));
    }

    private void userValidateAlreadyExistsEmail(User user) {
        if (userStorage.emailAlreadyExist(user.getEmail())) {
            Long emailId = userStorage.userIdByEmail(user.getEmail());
            if (!emailId.equals(user.getId())) {
                log.info("Email принадлежит пользователю " + userStorage.getById(emailId));
                throw new UserAlreadyExistException(String.format("Пользователь с email %s уже существует",
                        user.getEmail()));
            }
        }
    }
}