package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void testBookingResponseDto() throws IOException {

        var owner = new User(
                4L,
                "OwnerName",
                "user4@email.ru"
        );
        var item = new Item(
                3L,
                "ItemName",
                "ItemDescription",
                true,
                owner,
                null
        );

        var booker = new User(
                3L,
                "BookerName",
                "user3@email.ru"
        );

        var bookingResponseDto = new BookingResponseDto(
                2L,
                LocalDateTime.of(2022, Month.OCTOBER, 15, 0, 32, 22),
                LocalDateTime.of(2022, Month.OCTOBER, 15, 1, 32, 22),
                item,
                booker,
                BookingStatus.WAITING);

        var res = json.write(bookingResponseDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.start");
        assertThat(res).hasJsonPath("$.end");
        assertThat(res).hasJsonPath("$.item");
        assertThat(res).hasJsonPath("$.booker");
        assertThat(res).hasJsonPath("$.status");
        assertThat(res).extractingJsonPathNumberValue("$.id")
                .isEqualTo(bookingResponseDto.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingResponseDto.getStart().toString());
        assertThat(res).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingResponseDto.getEnd().toString());
        assertThat(res).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingResponseDto.getItem().getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.item.name")
                .isEqualTo(bookingResponseDto.getItem().getName());
        assertThat(res).extractingJsonPathStringValue("$.item.description")
                .isEqualTo(bookingResponseDto.getItem().getDescription());
        assertThat(res).extractingJsonPathBooleanValue("$.item.available")
                .isEqualTo(bookingResponseDto.getItem().getAvailable());
        assertThat(res).extractingJsonPathNumberValue("$.item.owner.id")
                .isEqualTo(bookingResponseDto.getItem().getOwner().getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.item.owner.name")
                .isEqualTo(bookingResponseDto.getItem().getOwner().getName());
        assertThat(res).extractingJsonPathStringValue("$.item.owner.email")
                .isEqualTo(bookingResponseDto.getItem().getOwner().getEmail());
        assertThat(res).extractingJsonPathStringValue("$.item.requestId").isNullOrEmpty();
        assertThat(res).extractingJsonPathNumberValue("$.booker.id")
                .isEqualTo(bookingResponseDto.getBooker().getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo(bookingResponseDto.getBooker().getName());
        assertThat(res).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo(bookingResponseDto.getBooker().getEmail());
        assertThat(res).extractingJsonPathStringValue("$.status")
                .isEqualTo(bookingResponseDto.getStatus().toString());
    }
}