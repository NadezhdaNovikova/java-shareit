package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponseDto add(long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto approve(long userId, long bookingId, boolean approve);

    BookingResponseDto getById(long userId, long bookingId);

    List<BookingResponseDto> getByUser(long userId, BookingState state, Pageable pageRequest);

    List<BookingResponseDto> getByOwner(long ownerId, BookingState state, Pageable pageRequest);
}