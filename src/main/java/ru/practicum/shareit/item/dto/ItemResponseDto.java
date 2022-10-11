package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemResponseDto extends ItemDto{
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;
    List<CommentDto> comments = new ArrayList<>();
}