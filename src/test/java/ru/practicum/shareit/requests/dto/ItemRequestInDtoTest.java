package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestInDtoTest {

    @Autowired
    private JacksonTester<ItemRequestInDto> json;

    @Test
    void testItemRequestInDto() throws IOException {

        var requester = new User(
                3L,
                "UserName",
                "user3@email.ru"
        );

        var itemRequestInDto = new ItemRequestInDto(
                1L,
                "request text",
                requester,
                LocalDateTime.of(2022, Month.OCTOBER, 15, 00, 32, 22)
        );

        var res = json.write(itemRequestInDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.requester");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestInDto.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestInDto.getDescription());
        assertThat(res).extractingJsonPathNumberValue("$.requester.id")
                .isEqualTo(itemRequestInDto.getRequester().getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.requester.name")
                .isEqualTo(itemRequestInDto.getRequester().getName());
        assertThat(res).extractingJsonPathStringValue("$.requester.email")
                .isEqualTo(itemRequestInDto.getRequester().getEmail());
        assertThat(res).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestInDto.getCreated().toString());
    }
}