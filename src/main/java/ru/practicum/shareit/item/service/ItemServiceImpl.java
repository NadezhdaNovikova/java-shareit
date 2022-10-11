package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentStorage;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingStorage bookingStorage;
    private final CommentStorage commentStorage;

    @Transactional
    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        return ItemMapper.toItemDto(itemStorage.save(ItemMapper.toItem(itemDto, user, null)));
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        checkUser(userId);
        Item oldItem = itemStorage.findById(itemDto.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemDto.getId())));
        if (oldItem.getOwner().getId() == userId) {
            if (itemDto.getName() != null && !(itemDto.getName().trim().isEmpty())) {
                oldItem.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !(itemDto.getDescription().trim().isEmpty())) {
                oldItem.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                oldItem.setAvailable(itemDto.getAvailable());
            }
        } else {
            throw new EntityNotFoundException(String.format("Пользователь с id %s не является владельцем " +
                    "данной вещи!", userId));
        }
        return ItemMapper.toItemDto(itemStorage.save(oldItem));
    }

    @Override
    public ItemDto findById(long itemId) {
        return ItemMapper.toItemDto(itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemId))));
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        checkUser(userId);
        return ItemMapper.toItemDtoList(itemStorage.findItemsByOwnerId(userId));
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        return ItemMapper.toItemDtoList(itemStorage.searchItemsByText(text));
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        User user = userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Пользователь с таким id не найден!"));
        Item item = itemStorage.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Вещь с таким id не найдена!"));
        LocalDateTime now = LocalDateTime.now();
        Booking booking = bookingStorage.findAllByBookerIdAAndItemId(userId, itemId, now).stream()
                .findFirst().orElseThrow(() -> new BookingStateException("Запрос на бронирование не найден!"));
        if (booking != null) {
            return CommentMapper.toCommentDto(commentStorage.save(new Comment(commentDto.getId(),
                    commentDto.getText(), item, user, LocalDateTime.now())));
        } else {
            throw new RuntimeException("Пользователь с id " + " не брал вещь с id" + " в аренду!");
        }
    }

    private User checkUser(long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d не найден!", userId)));
    }

    private void checkItemDtoFields(ItemDto itemDto) {
        String errorMessage = null;
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            errorMessage = "Item can't be without name!";
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            errorMessage = "Item can't be without description!";
        }
        if (itemDto.getAvailable() == null) {
            errorMessage = "Item can't be without available status!";
        }

        if (errorMessage != null) {
            throw new ValidationException(errorMessage);
        }
    }

    private void checkCommentAuthor(long userId, long itemId) {
        if (!bookingStorage.existsByItem_IdAndBooker_IdAndEndBefore(itemId, userId, LocalDateTime.now())) {
            throw new ValidationException(
                    String.format("User id=%d hasn't booking item id=%d or booking isn't finished yet!", userId, itemId)
            );
        }
    }
}