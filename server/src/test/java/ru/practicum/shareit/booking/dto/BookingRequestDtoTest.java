package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testBookingRequestDto() throws IOException {

        var bookingRequestDto = new BookingRequestDto(
                3L,
                LocalDateTime.of(2022, Month.OCTOBER, 15, 0, 32, 22),
                LocalDateTime.of(2022, Month.OCTOBER, 15, 1, 32, 22)
        );

        var res = json.write(bookingRequestDto);

        assertThat(res).hasJsonPath("$.itemId");
        assertThat(res).hasJsonPath("$.start");
        assertThat(res).hasJsonPath("$.end");
        assertThat(res).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingRequestDto.getItemId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingRequestDto.getStart().toString());
        assertThat(res).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingRequestDto.getEnd().toString());
    }
}