package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    List<BookingResponseDto> getBookingByUser(long userId, BookingStateIn state);
}