package ru.practicum.shareit.booking.service;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto add(long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto approve(long userId, long bookingId, boolean isApproved) throws ConstraintViolationException;

    BookingResponseDto getById(long userId, long bookingId);

    List<BookingResponseDto> getByUser(long userId, String state, Pageable pageRequest);

    List<BookingResponseDto> getByOwner(long ownerId, String state, Pageable pageRequest);
}