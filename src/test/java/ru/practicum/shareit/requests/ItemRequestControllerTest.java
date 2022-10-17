package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.dto.ItemRequestInDto;
import ru.practicum.shareit.requests.dto.ItemRequestOutDto;
import ru.practicum.shareit.requests.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    User user;
    ItemRequestOutDto itemRequestOutDto;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "userName", "user@email.ru");
        itemRequestOutDto = new ItemRequestOutDto(
                1L,
                "Request Description",
                LocalDateTime.of(2022, 10, 15, 17, 35, 22),
                Collections.emptyList()
        );
    }

    @Test
    void add() throws Exception {
        when(itemRequestService.add(anyLong(), any()))
                .thenReturn(itemRequestOutDto);
        ItemRequestInDto itemRequestInDto = new ItemRequestInDto(itemRequestOutDto.getId(),
                itemRequestOutDto.getDescription(), user,
                LocalDateTime.of(2022, 10, 15, 17, 35, 22));


        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestOutDto.getDescription())))
                .andExpect(jsonPath("$.created", is(itemRequestOutDto.getCreated().toString())));

        itemRequestInDto.setDescription("");

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestInDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByOwner() throws Exception {
        when(itemRequestService.getByOwner(anyLong()))
                .thenReturn(List.of(itemRequestOutDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestOutDto))));
    }

    @Test
    void getAll() throws Exception {

        ItemRequestOutDto itemRequestOutDto2 = new ItemRequestOutDto(2L, "request2 description",
                LocalDateTime.of(2022, 10, 17, 13, 22, 22), null);
        when(itemRequestService.getAll(anyLong(), any()))
                .thenReturn(List.of(itemRequestOutDto2));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestOutDto2))));
        verify(itemRequestService, times(1)).getAll(anyLong(), any());
    }

    @Test
    void getById() throws Exception {
        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestOutDto);

        mvc.perform(get("/requests/888")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestOutDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(itemRequestOutDto.getCreated().toString()), String.class));
    }
}