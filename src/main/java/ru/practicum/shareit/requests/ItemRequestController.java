package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
import ru.practicum.shareit.requests.dto.ItemRequestInDto;
import ru.practicum.shareit.requests.dto.ItemRequestOutDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestOutDto add(@RequestHeader(HEADER_USER_ID) Long userId,
                                 @Validated({Create.class}) @RequestBody ItemRequestInDto requestInDto) {
        ItemRequestOutDto request = itemRequestService.add(userId, requestInDto);
        log.info("Был добавлен новый запрос id: {}, описание: {}", request.getId(), request.getDescription());
        return request;
    }

    @GetMapping()
    public List<ItemRequestOutDto> getByOwner(@RequestHeader(HEADER_USER_ID) long userId) {
        return itemRequestService.getByOwner(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestOutDto getById(@RequestHeader(HEADER_USER_ID) long userId,
                                     @PathVariable long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAll(@RequestHeader(HEADER_USER_ID) long userId,
                                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @Positive @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {
        int page = from / size;
        return itemRequestService.getAll(userId, PageRequest.of(page, size));
    }
}