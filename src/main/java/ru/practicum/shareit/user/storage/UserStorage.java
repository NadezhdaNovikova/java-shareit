package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> findById(long userId);

    List<User> getAll();

    User add(User user);

    User update(User user);

    void delete(long userId);
}