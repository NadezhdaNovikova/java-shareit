package ru.practicum.shareit.exception;

public class BookingStateException extends IllegalArgumentException {
    public BookingStateException(String message) {
        super(message);
    }
}