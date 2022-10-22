package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {

    ItemService itemService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    User user;
    Item item;
    ItemRequest itemRequest;

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository);
        user = new User(1L, "User", "user@email.ru");
        itemRequest = new ItemRequest(1L, "request description", user,
                LocalDateTime.of(2022, 10, 14, 13, 17, 29));
        item = new Item(1L, "Item", "item description", true, user, null);

    }

    @Test
    void add() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        ItemDto itemDto1 = itemService.add(1L, itemDto);
        assertNotNull(itemDto1);
        assertEquals(ItemDto.class, itemDto1.getClass());
        assertEquals(item.getId(), itemDto1.getId());
        assertEquals(item.getName(), itemDto1.getName());
        assertEquals(item.getDescription(), itemDto1.getDescription());
        assertEquals(item.getAvailable(), itemDto1.getAvailable());
    }

    @Test
    void update() {
        Item updatedItem = new Item(item.getId(), "updateItemName", "updateItemDescription",
                item.getAvailable(), item.getOwner(), item.getRequest());
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(updatedItem)).thenReturn(updatedItem);
        ItemDto itemDto = ItemMapper.toItemDto(updatedItem);
        ItemDto itemDto1 = itemService.update(1, itemDto);
        assertNotNull(itemDto1);
        assertEquals(ItemDto.class, itemDto1.getClass());
        assertEquals(updatedItem.getId(), itemDto1.getId());
        assertEquals(updatedItem.getName(), itemDto1.getName());
        assertEquals(updatedItem.getDescription(), itemDto1.getDescription());
        assertEquals(updatedItem.getAvailable(), itemDto1.getAvailable());

    }

    @Test
    void findById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemsId(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(Collections.emptyList());

        ItemResponseDto itemDto = ItemMapper.toItemResponseDto(item,
                null,
                null,
                Collections.emptyList());
        ItemResponseDto itemDto1 = itemService.findById(1L, 1L);
        assertNotNull(itemDto1);
        assertEquals(ItemResponseDto.class, itemDto1.getClass());
        assertEquals(itemDto.getId(), itemDto1.getId());
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
        assertEquals(itemDto.getAvailable(), itemDto1.getAvailable());

    }

    @Test
    void getAllItemsByOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findItemsByOwnerId(1L, Pageable.ofSize(15)))
                .thenReturn(Collections.singletonList(item));
        when(bookingRepository.findAllByItemsId(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(Collections.emptyList());
        List<ItemResponseDto> itemsList1 = itemService.getAllItemsByOwner(1L, Pageable.ofSize(15));
        assertNotNull(itemsList1);
        assertEquals(1L, itemsList1.size());
        assertEquals(item.getId(), itemsList1.get(0).getId());
        assertEquals(item.getName(), itemsList1.get(0).getName());
        assertEquals(item.getDescription(), itemsList1.get(0).getDescription());
        assertEquals(item.getAvailable(), itemsList1.get(0).getAvailable());

    }

    @Test
    void searchItemsByText() {
        when(itemRepository.searchItemsByText("text", Pageable.ofSize(20)))
                .thenReturn(Collections.singletonList(item));

        List<ItemDto> items = itemService.searchItemsByText("text", Pageable.ofSize(20));
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        assertEquals(item.getName(), items.get(0).getName());
        assertEquals(item.getDescription(), items.get(0).getDescription());
        assertEquals(item.getAvailable(), items.get(0).getAvailable());
    }

    @Test
    void addComment() {
        Comment comment = new Comment(1L, "text comment", item, user,
                LocalDateTime.now());
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusMinutes(3),
                item, user, BookingStatus.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(Boolean.TRUE);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto commentDto = itemService.addComment(CommentMapper.toCommentDto(comment),
                user.getId(), item.getId());
        assertNotNull(commentDto);
        assertEquals(CommentDto.class, commentDto.getClass());
        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(user.getName(), commentDto.getAuthorName());
    }

    @Test
    void addItemWithWrongUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.add(10, itemDto));

        assertEquals("Пользователь с id = 10 не найден!", exception.getMessage());
    }

    @Test
    void addItemWithWrongRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        item.setRequest(itemRequest);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.add(user.getId(), itemDto));

        assertEquals("Запрос с id = 1 не найден!", exception.getMessage());
    }

    @Test
    void updateItemWithWrongUserId() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.update(10, itemDto));

        assertEquals("Пользователь с id = 10 не найден!", exception.getMessage());
    }

    @Test
    void updateItemWithWrongItemId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        ItemDto itemDto = ItemMapper.toItemDto(item);
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.update(10, itemDto));

        assertEquals("Вещь с id = 1 не найдена!", exception.getMessage());
    }

    @Test
    void findItemWithWrongId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> itemService.findById(10, user.getId()));

        assertEquals("Вещь с id = 10 не найдена!", exception.getMessage());
    }

    @Test
    void getItemResponseDto() {
        Booking lastBooking = new Booking(1L, LocalDateTime.of(2022, 10, 14, 13, 22, 22),
                LocalDateTime.of(2022, 9, 15, 13, 22, 22),
                item, user, BookingStatus.APPROVED);

        Booking nextBooking = new Booking(1L, LocalDateTime.of(2022, 10, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22),
                item, user, BookingStatus.APPROVED);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllByItemsId(anyLong())).thenReturn(List.of(lastBooking));
        when(commentRepository.findCommentsByItemId(anyLong())).thenReturn(Collections.emptyList());
        when(bookingRepository.findLastBookingByItemId(anyLong(), anyLong(), any()))
                .thenReturn(List.of(lastBooking));
        when(bookingRepository.findNextBookingByItemId(anyLong(), anyLong(), any()))
                .thenReturn(List.of(nextBooking));
        ItemResponseDto itemDto = ItemMapper.toItemResponseDto(item,
                lastBooking,
                nextBooking,
                Collections.emptyList());
        ItemResponseDto itemDto1 = itemService.findById(item.getId(), user.getId());
        assertNotNull(itemDto1);
        assertEquals(ItemResponseDto.class, itemDto1.getClass());
        assertEquals(itemDto.getId(), itemDto1.getId());
        assertEquals(itemDto.getName(), itemDto1.getName());
        assertEquals(itemDto.getDescription(), itemDto1.getDescription());
        assertEquals(itemDto.getAvailable(), itemDto1.getAvailable());
        assertEquals(itemDto.getLastBooking(), itemDto1.getLastBooking());
        assertEquals(itemDto.getNextBooking(), itemDto1.getNextBooking());
    }

    @Test
    void addItemWithWrongItemName() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setName("");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(1, itemDto));

        assertEquals("У вещи должно быть название!", exception.getMessage());
    }

    @Test
    void addItemWithWrongItemDescription() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setDescription("");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(1, itemDto));

        assertEquals("У вещи должно быть описание!", exception.getMessage());
    }

    @Test
    void addItemWithNullAvailable() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setAvailable(null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(1, itemDto));

        assertEquals("Отсутствует статус доступности вещи для аренды!", exception.getMessage());
    }

    @Test
    void addCommentForBookingIsActive() {
        Comment comment = new Comment(1L, "text comment", item, user,
                LocalDateTime.now());
        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().plusDays(3),
                item, user, BookingStatus.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(anyLong(), anyLong(), any()))
                .thenReturn(Boolean.FALSE);
        when(commentRepository.save(any())).thenReturn(comment);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(CommentMapper.toCommentDto(comment),
                        user.getId(), item.getId()));

        assertEquals("Пользователь id=1 не арендовал вещь id=1 или аренда не завершена!", exception.getMessage());
    }
}