package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        return ItemMapper.toItemDto(itemStorage.add(user, ItemMapper.toItem(itemDto, user, null)));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        checkUser(userId);
        Item oldItem = itemStorage.findById(itemDto.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemDto.getId())));
        if (oldItem.getOwner().getId() == userId) {
            if (itemDto.getName() != null && !(itemDto.getName().trim().isEmpty())) {
                oldItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !(itemDto.getDescription().trim().isEmpty())) {
                oldItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                oldItem.setAvailable(itemDto.getAvailable());
            }
        } else {
            throw new EntityNotFoundException(String.format("Пользователь с id %s не является владельцем " +
                    "данной вещи!", userId));
        }
        return ItemMapper.toItemDto(itemStorage.update(userId, oldItem));
    }

    @Override
    public ItemDto findById(long itemId) {
        return ItemMapper.toItemDto(itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemId))));
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        checkUser(userId);
        return ItemMapper.toItemDtoList(itemStorage.getAll(userId));
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return ItemMapper.toItemDtoList(itemStorage.searchItemsByText(text));
    }

    private User checkUser(long userId) {
        return userStorage.getById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d не найден!", userId)));
    }
}