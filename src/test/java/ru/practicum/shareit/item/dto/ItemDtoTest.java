package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    private final ItemDto itemDto = new ItemDto(
            1L,
            "item name",
            "item description",
            true,
            null
    );

    @Test
    void testItemDto() throws IOException {
        var res = json.write(itemDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.name");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.available");
        assertThat(res).hasJsonPath("$.requestId");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(res).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(res).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(res).extractingJsonPathStringValue("$.requestId").isNullOrEmpty();
    }
}