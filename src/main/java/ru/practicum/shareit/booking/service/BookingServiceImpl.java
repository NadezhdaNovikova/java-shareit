package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingStorage bookingStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Transactional
    @Override
    public BookingResponseDto add(long userId, BookingRequestDto bookingRequestDto) {
        checkBookingDate(bookingRequestDto);
        User booker = checkUser(userId);
        Item item = checkItem(bookingRequestDto.getItemId());
        checkItemOwner(userId, item);
        checkItemAvailable(item);
        Booking booking = new Booking(0, bookingRequestDto.getStart(), bookingRequestDto.getEnd(),
                item, booker, BookingStatus.WAITING);
        return BookingMapper.toBookingResponseDto(bookingStorage.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto approve(long userId, long bookingId, boolean approve) {
        checkUserExist(userId);
        Booking booking = checkBooking(bookingId);
        checkBookingStatus(booking);
        Item item = booking.getItem();
        checkAccessForApprove(userId, item);
        booking.setStatus(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingResponseDto(bookingStorage.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingResponseDto getById(long userId, long bookingId) {
        Booking booking = checkBooking(bookingId);
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new EntityNotFoundException("Поиск запроса на бронирование по id возможен только для автора запроса" +
                    " или для владельца вещи!");
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getByUser(long userId, BookingState state) {
        checkUserExist(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingStorage
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingStorage.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingStorage.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            default:
                return bookingStorage.findAllByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getByOwner(long ownerId, BookingState state) {
        checkUserExist(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingStorage.findAllCurrentByItemsOwnerId(ownerId, now, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingStorage.findAllPastByItemsOwnerId(ownerId, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingStorage.findAllFutureByItemsOwnerId(ownerId, now)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingStorage.findAllStatusByItemsOwnerId(ownerId, BookingStatus.WAITING)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingStorage.findAllStatusByItemsOwnerId(ownerId, BookingStatus.REJECTED)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            default:
                return bookingStorage.findAllByItemsOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
        }
    }

    private void checkBookingStatus(Booking booking) {
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BookingStateException(String.format("Бронирование id=%d уже находится в статусе %S!",
                    booking.getId(), booking.getStatus()));
        }
    }

    private void checkItemOwner(long userId, Item item) {
        if (userId == item.getOwner().getId()) {
            throw new EntityNotFoundException(String.format("Ошибка бронирования! Пользователь id=%d является " +
                    "владельцем вещи id=%d", userId, item.getId()));
        }
    }

    private void checkItemAvailable(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Вещь id=%d не доступна для бронирования!", item.getId()));
        }
    }

    private void checkAccessForApprove(long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new EntityNotFoundException(String.format("Ошибка смены статуса запроса на бронирование! Пользователь" +
                    " id=%d не является владельцем вещи id=%d!", userId, item.getId()));
        }
    }

    private void checkBookingDate(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Дата окончания не может быть раньше даты старта!");
        }
        if (bookingRequestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала не может быть раньше текущей даты!");
        }
        if (bookingRequestDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата окончания не может быть раньше текущей даты!");
        }
    }

    public User checkUser(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId)));
    }

    public Item checkItem(long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemId)));
    }

    public Booking checkBooking(long bookingId) {
        return bookingStorage.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Бронирование с id = %s не найдено!", bookingId)));
    }

    public void checkUserExist(long userId) {
        if (!userStorage.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId));
        }
    }
}