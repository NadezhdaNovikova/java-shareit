package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemResponseDto extends ItemDto {
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;
    List<CommentDto> comments = new ArrayList<>();

    public ItemResponseDto(long id,
                           @NotBlank(groups = {Create.class}) String name,
                           @NotBlank(groups = {Create.class}) String description,
                           @NotNull(groups = {Create.class}) Boolean available,
                           BookingItemDto lastBooking,
                           BookingItemDto nextBooking,
                           List<CommentDto> comments) {
        super(id, name, description, available);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}