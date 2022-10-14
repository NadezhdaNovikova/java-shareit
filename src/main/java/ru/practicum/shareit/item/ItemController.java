package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping()
    public ItemDto add(@RequestHeader(HEADER_USER_ID) long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.add(userId, itemDto);
        log.info("Была добавлена новая вещь {}, id {}", item.getName(), item.getId());
        return item;
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader(HEADER_USER_ID) long userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        itemDto.setId(itemId);
        ItemDto item = itemService.update(userId, itemDto);
        log.info("Была обновлена вещь {}, id {}", item.getName(), item.getId());
        return item;
    }

    @GetMapping("{itemId}")
    public ItemResponseDto findById(@RequestHeader(HEADER_USER_ID) long userId,
                                    @PathVariable long itemId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping()
    public List<ItemResponseDto> getAll(@RequestHeader(HEADER_USER_ID) long userId,
                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                            Integer from,
                                        @Positive @RequestParam(name = "size", defaultValue = "10")
                                            Integer size) {
        int page = from / size;
        return itemService.getAllItemsByOwner(userId, PageRequest.of(page, size));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam("text") String text,
                                           @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                               Integer from,
                                           @Positive @RequestParam(name = "size", defaultValue = "10")
                                               Integer size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        int page = from / size;
        return itemService.searchItemsByText(text, PageRequest.of(page, size));
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@RequestHeader(HEADER_USER_ID) long userId,
                             @Validated({Create.class}) @RequestBody CommentDto commentDto,
                             @PathVariable long itemId) {
        CommentDto comment = itemService.addComment(commentDto, userId, itemId);
        log.info("Для вещи c id {} пользователь id {} добавил новый отзыв", itemId, userId);
        return comment;
    }
}