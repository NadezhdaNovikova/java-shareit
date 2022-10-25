package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.requests.dto.ItemRequestInDto;
import ru.practicum.shareit.requests.dto.ItemRequestOutDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestOutDto add(@RequestHeader(HEADER_USER_ID) Long userId,
                                 @RequestBody ItemRequestInDto requestInDto) {
        ItemRequestOutDto itemRequestOutDto = itemRequestService.add(userId, requestInDto);
        return itemRequestOutDto;
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
                                                @RequestParam(name = "from", defaultValue = "0")
                                                Integer from,
                                                @RequestParam(name = "size", defaultValue = "10")
                                                Integer size) {
        int page = from / size;
        return itemRequestService.getAll(userId, PageRequest.of(page, size));
    }
}