package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserStorageInMemory implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emailUniqSet = new HashMap<>();
    private long id = 0;

    @Override
    public Optional<User> getById(long userId) {
        if (users.containsKey(userId)) {
            return Optional.of(users.get(userId));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User add(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        emailUniqSet.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        emailUniqSet.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }

    @Override
    public boolean emailAlreadyExist(String email) {
        return emailUniqSet.containsKey(email);
    }

    @Override
    public Long userIdByEmail(String email) {
        return emailUniqSet.get(email);
    }

    @Override
    public void deleteEmailUnique(String email) {
        emailUniqSet.remove(email);
    }
}