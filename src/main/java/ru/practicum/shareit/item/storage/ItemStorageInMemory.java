package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 0L;

    @Override
    public Item add(User user, Item item) {
        item.setId(++id);
        item.setOwner(user);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(long userId, Item item) {
        Item oldItem = items.get(item.getId());
        if (oldItem != null) {
            if (oldItem.getOwner().getId() == userId) {
                if (item.getName() != null && !(item.getName().trim().isEmpty())) {
                    oldItem.setName(item.getName());
                }
                if (item.getDescription() != null && !(item.getDescription().trim().isEmpty())) {
                    oldItem.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    oldItem.setAvailable(item.getAvailable());
                }
                items.put(oldItem.getId(), oldItem);
                return oldItem;
            } else {
                throw new EntityNotFoundException(String.format("Пользователь с id %s не является владельцем " +
                        "данной вещи!", userId));
            }
        } else {
            throw new EntityNotFoundException("Вещь не найдена!");
        }
    }

    @Override
    public Optional<Item> findById(long itemId) {
        if (items.containsKey(itemId)) {
            return Optional.of(items.get(itemId));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> getAll(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItemsByText(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable())
                .collect(Collectors.toList());
    }
}