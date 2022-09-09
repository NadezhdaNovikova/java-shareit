package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping()
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.add(userId, itemDto);
        log.info("Была добавлена новая вещь {}, id {}", item.getName(), item.getId());
        return item;
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @PathVariable long itemId) {
        itemDto.setId(itemId);
        ItemDto item = itemService.update(userId, itemDto);
        log.info("Была обновлена вещь {}, id {}", item.getName(), item.getId());
        return item;
    }

    @GetMapping("{itemId}")
    public ItemDto findById(@PathVariable long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping()
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByText(@RequestParam("text") String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchItemsByText(text);
    }
}