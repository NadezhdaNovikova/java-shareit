package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingRequestDto {

    private Long itemId;
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime end;

}