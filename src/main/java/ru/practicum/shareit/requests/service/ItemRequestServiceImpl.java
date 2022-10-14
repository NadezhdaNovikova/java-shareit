package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestInDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.dto.ItemRequestOutDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestOutDto add(long userId, ItemRequestInDto requestInDto) {
        User requester = checkUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestInDto, requester);
        return ItemRequestMapper.toItemRequestOutDto(itemRequestRepository.save(itemRequest), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestOutDto> getByOwner(long userId) {
        checkUserExist(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return toListRequestOutDto(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestOutDto> getAll(long userId, PageRequest pageRequest) {
        checkUserExist(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByOtherUsers(userId, pageRequest);
        return toListRequestOutDto(requests);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestOutDto getById(long userId, long requestId) {
        checkUserExist(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Запрос с id = %s не найдена!", requestId)));
        return ItemRequestMapper.toItemRequestOutDto(itemRequest,
                itemRepository.findAllByRequestId(itemRequest.getId()));
    }


    private List<ItemRequestOutDto> toListRequestOutDto(List<ItemRequest> requests) {
        List<ItemRequestOutDto> requestsOut;
        requestsOut = requests.stream()
                .map(request -> ItemRequestMapper.toItemRequestOutDto(request,
                        itemRepository.findAllByRequestId(request.getId())))
                .collect(Collectors.toList());
        return requestsOut;
    }
    public User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId)));
    }

    public void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId));
        }
    }
}
