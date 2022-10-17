package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto add(long userId, ItemDto itemDto);

    ItemDto update(long userId, ItemDto itemDto);

    ItemResponseDto findById(long itemId, long userId);

    List<ItemResponseDto> getAllItemsByOwner(long userId, Pageable pageRequest);

    List<ItemDto> searchItemsByText(String text, Pageable pageRequest);

    CommentDto addComment(CommentDto commentDto, long userId, long itemId);
}