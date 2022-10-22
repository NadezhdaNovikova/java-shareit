package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestInDto;
import ru.practicum.shareit.requests.dto.ItemRequestOutDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {

    ItemRequestService itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    User user;
    Item item;
    ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository);
        user = new User(1L, "User", "user@email.ru");
        itemRequest = new ItemRequest(1L, "request description", user,
                LocalDateTime.of(2022, 10, 14, 7, 24, 24));
        item = new Item(1L, "Item", "item description", true, user, null);
    }

    @Test
    void add() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestInDto requestDto = new ItemRequestInDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated());
        ItemRequestOutDto itemRequestOutDto = itemRequestService.add(user.getId(), requestDto);
        assertNotNull(itemRequestOutDto);
        assertEquals(ItemRequestOutDto.class, itemRequestOutDto.getClass());
        assertEquals(itemRequest.getId(), itemRequestOutDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestOutDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestOutDto.getCreated());

    }

    @Test
    void addRequestWithWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemRequestInDto requestDto = new ItemRequestInDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated());
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.add(999L, requestDto));

        assertEquals("Пользователь с id = 999 не найден!", exception.getMessage());

    }

    @Test
    void getByOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestOutDto> itemRequestOutDto = itemRequestService.getByOwner(user.getId());
        assertNotNull(itemRequestOutDto);
        assertEquals(1, itemRequestOutDto.size());
        assertEquals(ItemRequestOutDto.class, itemRequestOutDto.get(0).getClass());
        assertEquals(itemRequest.getId(), itemRequestOutDto.get(0).getId());
        assertEquals(itemRequest.getDescription(), itemRequestOutDto.get(0).getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestOutDto.get(0).getCreated());
    }

    @Test
    void getAll() {
        User user2 = new User(2L, "User2", "user2@email.ru");
        ItemRequest itemRequest2 = new ItemRequest(2L, "request description2", user2,
                LocalDateTime.of(2022, 10, 14, 13, 44, 22));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByOtherUsers(anyLong(), any())).thenReturn(List.of(itemRequest2));
        List<ItemRequestOutDto> itemRequestOutDtos = itemRequestService.getAll(user.getId(),
                Pageable.ofSize(10));
        assertNotNull(itemRequestOutDtos);
        assertEquals(1, itemRequestOutDtos.size());
        assertEquals(ItemRequestOutDto.class, itemRequestOutDtos.get(0).getClass());
        assertEquals(itemRequest2.getId(), itemRequestOutDtos.get(0).getId());
        assertEquals(itemRequest2.getDescription(), itemRequestOutDtos.get(0).getDescription());
        assertEquals(itemRequest2.getCreated(), itemRequestOutDtos.get(0).getCreated());

    }

    @Test
    void getById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));
        ItemRequestOutDto itemRequestOutDto = itemRequestService.getById(user.getId(), itemRequest.getId());
        assertNotNull(itemRequestOutDto);
        assertEquals(ItemRequestOutDto.class, itemRequestOutDto.getClass());
        assertEquals(1, itemRequestOutDto.getItems().size());
        assertEquals(itemRequest.getId(), itemRequestOutDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestOutDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestOutDto.getCreated());
    }

    @Test
    void findAllByOwnerWithWrongOwnerId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.getByOwner(10));

        assertEquals("Пользователь с id = 10 не найден!", exception.getMessage());
    }

    @Test
    void findByWrongRequestId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemRequestService.getById(user.getId(), 12));

        assertEquals("Запрос с id = 12 не найден!", exception.getMessage());
    }
}