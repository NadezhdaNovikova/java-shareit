package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingItemDtoTest {

    @Autowired
    private JacksonTester<BookingItemDto> json;

    @Test
    void testBookingItemDto() throws IOException {

        var bookingItemDto = new BookingItemDto(
                1L,
                3L);

        var res = json.write(bookingItemDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.bookerId");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(bookingItemDto.getId().intValue());
        assertThat(res).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingItemDto.getBookerId().intValue());
    }
}