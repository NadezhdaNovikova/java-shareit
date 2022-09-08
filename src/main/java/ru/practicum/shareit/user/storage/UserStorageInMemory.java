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
    private long id = 0;

    @Override
    public Optional<User> findById(long userId) {
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
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        if (user.getName() != null && !(user.getName().trim().isBlank())) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !(user.getEmail().trim().isBlank())) {
            oldUser.setEmail(user.getEmail());
        }
        users.put(user.getId(), oldUser);

        return oldUser;
    }

    @Override
    public void delete(long userId) {
        users.remove(userId);
    }
}