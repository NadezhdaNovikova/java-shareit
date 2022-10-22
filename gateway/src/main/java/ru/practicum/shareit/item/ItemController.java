package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> add(@RequestHeader(HEADER_USER_ID) long userId,
                                      @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Добавление новой вещи {}", itemDto);
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER_USER_ID) long userId,
                                         @Validated({Update.class}) @RequestBody ItemDto itemDto,
                                         @PathVariable long itemId) {
        itemDto.setId(itemId);
        log.info("Обновление вещи {}, id {}", itemDto.getName(), itemId);
        return itemClient.update(userId, itemDto);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(HEADER_USER_ID) long userId,
                                           @PathVariable long itemId) {
        log.info("Получение вещи id {}", itemId);
        return itemClient.findById(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER_USER_ID) long userId,
                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                         Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10")
                                         Integer size) {
        log.info("Обновление всех вещей пользователя с id {}, from = {}, size = {}", userId, from, size);
        return itemClient.getAllItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByText(@RequestParam("text") String text,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                    Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10")
                                                    Integer size) {
        log.info("Поиск вещей по тексту \"{}\", from = {}, size = {}", text, from, size);
        return itemClient.searchItemsByText(text, from, size);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER_USER_ID) long userId,
                                             @Validated({Create.class}) @RequestBody CommentDto commentDto,
                                             @PathVariable long itemId) {
        commentDto.setCreated(LocalDateTime.now());
        log.info("Добавление отзыва для вещи с id {}", itemId);
        return itemClient.addComment(commentDto, userId, itemId);
    }
}