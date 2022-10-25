package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BookingMapperTest {

    User user;
    User user2;
    Item item;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "User1", "user@email.ru");
        user2 = new User(2L, "User2", "user2@email.ru");
        item = new Item(1L, "itemName", "item description", true, user2, null);
        booking = new Booking(1L, LocalDateTime.of(2022, 9, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22), item, user,
                BookingStatus.WAITING);
    }

    @Test
    void toBookingItemDto() {
        BookingItemDto bookingDto = BookingMapper.toBookingItemDto(booking);
        assertNotNull(bookingDto);
        assertEquals(BookingItemDto.class, bookingDto.getClass());
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }

    @Test
    void toBooking() {
        BookingRequestDto bookingDto = new BookingRequestDto(item.getId(),
                LocalDateTime.of(2022, 9, 16, 13, 22, 22),
                LocalDateTime.of(2022, 9, 17, 13, 22, 22));
        Booking newBooking = BookingMapper.toBooking(bookingDto, item, user);
        assertNotNull(newBooking);
        assertEquals(Booking.class, newBooking.getClass());
        assertEquals(booking.getBooker(), newBooking.getBooker());
        assertEquals(booking.getItem(), newBooking.getItem());
        assertEquals(booking.getStatus(), newBooking.getStatus());
        assertEquals(booking.getStart(), newBooking.getStart());
        assertEquals(booking.getEnd(), newBooking.getEnd());
    }
}