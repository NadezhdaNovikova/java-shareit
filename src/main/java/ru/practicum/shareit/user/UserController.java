package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @PostMapping()
    public UserDto add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        UserDto user = userService.add(userDto);
        log.info("Добавлен новый пользователь {}, id {}, email {}", user.getName(), user.getId(), user.getEmail());
        return user;
    }

    @PatchMapping("{userId}")
    public UserDto update(@PathVariable long userId,
                          @Validated({Update.class}) @RequestBody UserDto userDto) {
        userDto.setId(userId);
        UserDto user = userService.update(userDto);
        log.info("Пользователь {}, id {}, email {} обновлен", user.getName(), user.getId(), user.getEmail());
        return user;
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
        log.info("Пользователь с id {} удален!", userId);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return userService.getById(userId);
    }
}