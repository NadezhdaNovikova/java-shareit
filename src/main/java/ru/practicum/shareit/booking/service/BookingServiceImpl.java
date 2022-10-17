package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingResponseDto add(long userId, BookingRequestDto bookingRequestDto) {
        checkBookingDate(bookingRequestDto);
        User booker = checkUser(userId);
        Item item = checkItem(bookingRequestDto.getItemId());
        checkItemOwner(userId, item);
        checkItemAvailable(item);
        return BookingMapper.toBookingResponseDto(bookingRepository.save(BookingMapper.toBooking(bookingRequestDto,
                item,booker)));
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
        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
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
    public List<BookingResponseDto> getByUser(long userId, BookingState state, Pageable pageRequest) {
        checkUserExist(userId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, now, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING,
                                pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED,
                                pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponseDto> getByOwner(long ownerId, BookingState state, Pageable pageRequest) {
        checkUserExist(ownerId);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case CURRENT:
                return bookingRepository.findAllCurrentByItemsOwnerId(ownerId, now, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllPastByItemsOwnerId(ownerId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllFutureByItemsOwnerId(ownerId, now, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllStatusByItemsOwnerId(ownerId, BookingStatus.WAITING, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllStatusByItemsOwnerId(ownerId, BookingStatus.REJECTED, pageRequest)
                        .stream()
                        .map(BookingMapper::toBookingResponseDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findAllByItemsOwnerId(ownerId, pageRequest).stream()
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
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId)));
    }

    public Item checkItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemId)));
    }

    public Booking checkBooking(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Бронирование с id = %s не найдено!", bookingId)));
    }

    public void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Пользователь с id = %s не найден!", userId));
        }
    }
}