package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    @MockBean
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

    @MockBean
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    @MockBean
    private final ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    private final BookingService bookingService =
            new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

    User user;
    User user2;
    Item item;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(11L, "userName", "user@email.ru");
        user2 = new User(21L, "userName2", "user2@email.ru");
        item = new Item(11L, "itemName", "item description", true, user2, null);
        booking = new Booking(11L, LocalDateTime.of(2022, 10, 17, 13, 22, 22),
                LocalDateTime.of(2022, 10, 18, 13, 22, 22), item, user,
                BookingStatus.WAITING);
    }

    @Test
    void add() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(item.getId(), booking.getStart(),
                booking.getEnd());
        BookingResponseDto bookingDto = bookingService.add(user.getId(), bookingRequestDto);
        assertNotNull(bookingDto);
        assertEquals(BookingResponseDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void addWhenEndIsBeforeStart() {

        booking.setEnd(booking.getStart().minusDays(10));
        BookingRequestDto bookingRequestDto = new BookingRequestDto(item.getId(), booking.getStart(),
                booking.getEnd());
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.add(user.getId(), bookingRequestDto));

        assertEquals("Дата окончания не может быть раньше даты старта!", exception.getMessage());
    }

    @Test
    void approve() {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto bookingResponseDto = bookingService.approve(user2.getId(), booking.getId(), true);
        assertNotNull(bookingResponseDto);
        assertEquals(BookingResponseDto.class, bookingResponseDto.getClass());
        assertEquals(booking.getId(), bookingResponseDto.getId());
        assertEquals(booking.getBooker().getId(), bookingResponseDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingResponseDto.getItem().getId());
        assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus());
    }

    @Test
    void getById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingResponseDto bookingDto = bookingService.getById(user.getId(), booking.getId());
        assertNotNull(bookingDto);
        assertEquals(BookingResponseDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    void findBookingWithWrongId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getById(user.getId(), 100));

        assertEquals("Бронирование с id = 100 не найдено!", exception.getMessage());
    }

    @Test
    void getByIdWrongUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getById(10, booking.getId()));

        assertEquals("Поиск запроса на бронирование по id возможен только для автора запроса" +
                " или для владельца вещи!", exception.getMessage());
    }

    @Test
    void getByUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> bookings = bookingService.getByUser(user.getId(), "ALL",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByUser(user.getId(), "CURRENT",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByUser(user.getId(), "PAST",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByUser(user.getId(), "FUTURE",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByUser(user.getId(), "WAITING",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByUser(user.getId(), "REJECTED",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.getByUser(user.getId(), "UNSUPPORTED_STATUS", Pageable.ofSize(10)));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void getByOwner() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemsOwnerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllCurrentByItemsOwnerId(anyLong(),
                any(), any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllPastByItemsOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllFutureByItemsOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllStatusByItemsOwnerId(anyLong(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> bookings = bookingService.getByOwner(user2.getId(), "ALL",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByOwner(user2.getId(), "CURRENT",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByOwner(user2.getId(), "PAST",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByOwner(user2.getId(), "FUTURE",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByOwner(user2.getId(), "WAITING",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        bookings = bookingService.getByOwner(user2.getId(), "REJECTED",
                Pageable.ofSize(10));
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking.getBooker().getId(), bookings.get(0).getBooker().getId());
        assertEquals(booking.getItem().getId(), bookings.get(0).getItem().getId());
        assertEquals(booking.getStatus(), bookings.get(0).getStatus());

        BookingStateException exception = assertThrows(BookingStateException.class, () ->
                bookingService.getByOwner(user.getId(), "UNSUPPORTED_STATUS", Pageable.ofSize(10)));
        assertEquals("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }

    @Test
    void approveNotWaiting() {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        Booking bookingApproved = booking;
        bookingApproved.setStatus(BookingStatus.APPROVED);

        BookingStateException exception = assertThrows(
                BookingStateException.class,
                () -> bookingService.approve(user2.getId(), bookingApproved.getId(), true));

        assertEquals("Бронирование id=11 уже находится в статусе APPROVED!", exception.getMessage());
    }

    @Test
    void approveNotOwner() {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.approve(user.getId(), booking.getId(), true));

        assertEquals("Ошибка смены статуса запроса на бронирование! " +
                "Пользователь id=11 не является владельцем вещи id=11!", exception.getMessage());
    }

    @Test
    void addBookerIsOwnerByItem() {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(userRepository.save(any())).thenReturn(user2);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingRequestDto bookingRequestDto = new BookingRequestDto(item.getId(), booking.getStart(),
                booking.getEnd());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.add(user2.getId(), bookingRequestDto));

        assertEquals("Ошибка бронирования! Пользователь id=21 является владельцем вещи id=11",
                exception.getMessage());
    }
}