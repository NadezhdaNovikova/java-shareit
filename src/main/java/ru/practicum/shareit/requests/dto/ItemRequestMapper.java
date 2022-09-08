package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.model.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated()
        );
    }
}