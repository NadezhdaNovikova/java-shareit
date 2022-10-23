package ru.practicum.shareit.requests.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestOutDto toItemRequestOutDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestOutDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList())
        );
    }

    public static ItemRequest toItemRequest(ItemRequestInDto request, User requester) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }
}