package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(HEADER_USER_ID) Long userId,
                                      @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Добавление нового запроса, описание: {}", itemRequestDto.getDescription());
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping()
    public ResponseEntity<Object>  getByOwner(@RequestHeader(HEADER_USER_ID) long userId) {
        log.info("Получение списка запросов пользователя с id = {}", userId);
        return itemRequestClient.getByOwner(userId);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object>  getById(@RequestHeader(HEADER_USER_ID) long userId,
                                     @PathVariable long requestId) {
        log.info("Получение запроса id = {}", requestId);
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object>  getAll(@RequestHeader(HEADER_USER_ID) long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {
        log.info("Получение списка запросов пользователя с id = {}", userId);
        return itemRequestClient.getAll(userId, from, size);
    }
}