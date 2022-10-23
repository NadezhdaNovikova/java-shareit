package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto item, User user, @Nullable ItemRequest request) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                user,
                request
        );
    }

    public static ItemResponseDto toItemResponseDto(Item item, Booking lastBooking, Booking nextBooking,
                                                    List<CommentDto> comments) {
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking != null ? BookingMapper.toBookingItemDto(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingItemDto(nextBooking) : null,
                comments);
    }
}