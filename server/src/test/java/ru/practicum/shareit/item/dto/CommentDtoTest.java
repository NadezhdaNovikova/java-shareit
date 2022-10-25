package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private final CommentDto commentDto = new CommentDto(
            1L,
            "comment text",
            "UserName",
            LocalDateTime.of(2022, Month.OCTOBER, 15, 00, 32, 22)
    );

    @Test
    void testCommentDto() throws IOException {
        var res = json.write(commentDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.text");
        assertThat(res).hasJsonPath("$.authorName");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(commentDto.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.text").isEqualTo(commentDto.getText());
        assertThat(res).extractingJsonPathStringValue("$.authorName").isEqualTo(commentDto.getAuthorName());
        assertThat(res).extractingJsonPathStringValue("$.created")
                .isEqualTo(commentDto.getCreated().toString());
    }
}