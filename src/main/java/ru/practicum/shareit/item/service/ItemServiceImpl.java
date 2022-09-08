package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        return ItemMapper.toItemDto(itemStorage.add(user, ItemMapper.toItem(itemDto, user)));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        return ItemMapper.toItemDto(itemStorage.update(userId, ItemMapper.toItem(itemDto, user)));

    }

    @Override
    public ItemDto findById(long itemId) {
        return ItemMapper.toItemDto(itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с таким id = %s не найдена!", itemId))));
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        checkUser(userId);
        return itemStorage.getAll(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return itemStorage.searchItemsByText(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private User checkUser(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d не найден!", userId)));
    }
}