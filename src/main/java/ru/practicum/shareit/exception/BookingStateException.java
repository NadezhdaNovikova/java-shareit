package ru.practicum.shareit.exception;

public class BookingStateException extends IllegalStateException{

    public BookingStateException(String message) {
        super(message);
    }
}