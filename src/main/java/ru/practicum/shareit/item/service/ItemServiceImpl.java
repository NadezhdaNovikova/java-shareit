package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User user = checkUser(userId);
        checkItemDto(itemDto);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new EntityNotFoundException(String.format("Запрос с id = %d не найден!", itemDto.getRequestId())));
        }
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto, user, request)));
    }

    @Transactional
    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        checkUser(userId);
        Item oldItem = checkItem(itemDto.getId());
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
                    "вещи id %s!", userId, itemDto.getId()));
        }
        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public ItemResponseDto findById(long itemId, long userId) {
        checkUser(userId);
        Item item = checkItem(itemId);
        return getItemResponseDto(item, userId);
    }


    @Transactional(readOnly = true)
    @Override
    public List<ItemResponseDto> getAllItemsByOwner(long userId, Pageable pageRequest) {
        checkUser(userId);
        List<Item> itemList = itemRepository.findItemsByOwnerId(userId, pageRequest);
        List<ItemResponseDto> itemDtoResponseList = new ArrayList<>();
        for (Item item : itemList) {
            ItemResponseDto itemResponseDto = getItemResponseDto(item, userId);
            itemDtoResponseList.add(itemResponseDto);
        }
        return itemDtoResponseList;
    }

    @Override
    public List<ItemDto> searchItemsByText(String text, Pageable pageRequest) {
        return itemRepository.searchItemsByText(text, pageRequest).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(CommentDto commentDto, long userId, long itemId) {
        User user = checkUser(userId);
        Item item = checkItem(itemId);
        checkCommentAuthor(userId, itemId);
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Пустой отзыв! Должен быть текст!");
        }
        Comment comment = new Comment(commentDto.getId(),
                commentDto.getText(), item, user, LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private ItemResponseDto getItemResponseDto(Item item, long userId) {
        List<Booking> bookingList;
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking;
        Booking nextBooking;
        bookingList = bookingRepository.findAllByItemsId(item.getId());
        List<CommentDto> comments = commentRepository.findCommentsByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        if (bookingList.isEmpty()) {
            lastBooking = null;
            nextBooking = null;
        } else {
            lastBooking = bookingRepository.findLastBookingByItemId(item.getId(), userId, now)
                    .stream().findFirst().orElse(null);
            nextBooking = bookingRepository.findNextBookingByItemId(item.getId(), userId, now)
                    .stream().findFirst().orElse(null);
        }
        return ItemMapper.toItemResponseDto(item, lastBooking, nextBooking, comments);
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Пользователь с id = %d не найден!", userId)));
    }

    private Item checkItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Вещь с id = %s не найдена!", itemId)));
    }

    private void checkItemDto(ItemDto itemDto) {
        String error = null;
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            error = "У вещи должно быть название!";
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            error = "У вещи должно быть описание!";
        }
        if (itemDto.getAvailable() == null) {
            error = "Отсутствует статус доступности вещи для аренды!";
        }

        if (error != null) {
            throw new ValidationException(error);
        }
    }

    private void checkCommentAuthor(long userId, long itemId) {
        if (!(bookingRepository.existsByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now()))) {
            throw new ValidationException(
                    String.format("Пользователь id=%d не арендовал вещь id=%d или аренда не завершена!", userId, itemId)
            );
        }
    }
}