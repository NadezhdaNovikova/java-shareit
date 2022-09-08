package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    ItemDto findById(long itemId);

    List<ItemDto> getAll(long userId);

    List<ItemDto> searchItemsByText(String text);
}