package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {

    @Autowired
    private JacksonTester<UserDto> json;

    private final UserDto userDto = new UserDto(
            1L,
            "UserName",
            "user1@ya.ru"
    );

    @Test
    void testUserDto() throws IOException {
        var res = json.write(userDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.name");
        assertThat(res).hasJsonPath("$.email");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(res).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}