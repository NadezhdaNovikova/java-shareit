package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    ObjectMapper mapper;

    BookingResponseDto bookingResponseDto;

    User user;
    Item item;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "user name", "user@email.ru");
        item = new Item(1L, "item1", "description1", true, user, null);
        bookingResponseDto = new BookingResponseDto(1L,
                LocalDateTime.of(2022, 10, 16, 21, 22, 22),
                LocalDateTime.of(2022, 10, 17, 21, 22, 22),
                item, user, BookingStatus.WAITING);
    }

    @Test
    void add() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(
                1L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(1));
        when(bookingService.add(anyLong(), any()))
                .thenReturn(bookingResponseDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).add(anyLong(), any());
    }

    @Test
    void approve() throws Exception {
        bookingResponseDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingResponseDto);

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).approve(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingResponseDto);

        mvc.perform(get("/bookings/888")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingResponseDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingResponseDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).getById(anyLong(), anyLong());
    }

    @Test
    void getByUser() throws Exception {
        when(bookingService.getByUser(anyLong(), any(), any()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).getByUser(anyLong(), any(), any());
    }

    @Test
    void getByOwner() throws Exception {
        when(bookingService.getByOwner(anyLong(), any(), any()))
                .thenReturn(List.of(bookingResponseDto));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingResponseDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookingResponseDto.getEnd().toString())))
                .andExpect(jsonPath("$[0].status", is(bookingResponseDto.getStatus().toString())));

        verify(bookingService, times(1)).getByOwner(anyLong(), any(), any());
    }

}