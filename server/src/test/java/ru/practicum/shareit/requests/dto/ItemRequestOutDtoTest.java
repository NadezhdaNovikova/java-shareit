package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestOutDtoTest {

    @Autowired
    private JacksonTester<ItemRequestOutDto> json;

    @Test
    void testItemRequestOutDto() throws IOException {

        var item = new ItemDto(
                3L,
                "ItemName",
                "ItemDescription",
                true,
                1L
        );
        List<ItemDto> items = new ArrayList<>();
        items.add(item);

        var itemRequestOutDto = new ItemRequestOutDto(
                1L,
                "comment text",
                LocalDateTime.of(2022, Month.OCTOBER, 15, 0, 32, 22),
                items);

        var res = json.write(itemRequestOutDto);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.description");
        assertThat(res).hasJsonPath("$.created");
        assertThat(res).hasJsonPath("$.items");
        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(itemRequestOutDto.getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestOutDto.getDescription());
        assertThat(res).extractingJsonPathStringValue("$.created")
                .isEqualTo(itemRequestOutDto.getCreated().toString());
        assertThat(res).extractingJsonPathArrayValue("$.items").isInstanceOf(ArrayList.class);
        assertThat(res).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestOutDto.getItems().get(0).getId().intValue());
        assertThat(res).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestOutDto.getItems().get(0).getName());
        assertThat(res).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestOutDto.getItems().get(0).getDescription());
        assertThat(res).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemRequestOutDto.getItems().get(0).getAvailable());
        assertThat(res).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(itemRequestOutDto.getItems().get(0).getRequestId().intValue());
    }

}