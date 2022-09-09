package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemStorageInMemory implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemLists = new LinkedHashMap<>();
    private Long id = 0L;

    @Override
    public Item add(User user, Item item) {
        final List<Item> itemsList = userItemLists.computeIfAbsent(item.getOwner().getId(), k -> new ArrayList<>());
        item.setId(++id);
        item.setOwner(user);
        items.put(item.getId(), item);
        itemsList.add(item);
        userItemLists.put(user.getId(), itemsList);
        return item;
    }

    @Override
    public Item update(long userId, Item item) {
        Item oldItem = items.get(item.getId());
        final List<Item> itemsList = userItemLists.get(userId);
        itemsList.remove(oldItem);
        itemsList.add(item);
        items.put(item.getId(), item);
        userItemLists.put(userId, itemsList);
        return item;

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
        if (userItemLists.containsKey(userId)) {
            return userItemLists.get(userId);
        }
        return Collections.emptyList();
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