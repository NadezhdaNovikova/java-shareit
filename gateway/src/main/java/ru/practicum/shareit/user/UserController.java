package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping()
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @PostMapping()
    public ResponseEntity<Object>  add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("Добавление нового пользователя {}", userDto);
        return userClient.add(userDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object>  update(@PathVariable long userId,
                          @Validated({Update.class}) @RequestBody UserDto userDto) {
        userDto.setId(userId);
        log.info("Обновление пользователя id = {}, {}", userId, userDto);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public void delete(@PathVariable long userId) {
        userClient.delete(userId);
        log.info("Пользователь с id {} удален!", userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object>  getById(@PathVariable long userId) {
        return userClient.getById(userId);
    }
}