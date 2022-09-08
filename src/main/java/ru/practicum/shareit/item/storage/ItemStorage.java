package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {

    Item add(User user, Item item);

    Item update(long userId, Item item);

    Optional<Item> findById(long itemId);

    List<Item> getAll(long userId);

    List<Item> searchItemsByText(String text);
}