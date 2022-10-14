package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.requests.dto.ItemRequestInDto;
import ru.practicum.shareit.requests.dto.ItemRequestOutDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestOutDto add(long userId, ItemRequestInDto requestInDto);

    List<ItemRequestOutDto> getByOwner(long userId);

    List<ItemRequestOutDto> getAll(long userId, PageRequest pageRequest);

    ItemRequestOutDto getById(long userId, long requestId);
}