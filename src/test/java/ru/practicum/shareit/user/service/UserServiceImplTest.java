package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    User user = new User(
            1L,
            "userName",
            "user@email.com"
    );

    @MockBean
    UserRepository userRepository;
    UserService userService;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getAll() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));
        List<UserDto> userDtos = userService.getAll();

        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());
        assertThat(userDtos, equalTo(List.of(UserMapper.toUserDto(user))));
    }

    @Test
    void add() {
        when(userRepository.save(any()))
                .thenReturn(user);
        UserDto userDto = userService.add(UserMapper.toUserDto(user));

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void update() {
        when(userRepository.save(any()))
                .thenReturn(user);
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.update(UserMapper.toUserDto(user));

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getById() {
        when(userRepository.save(any()))
                .thenReturn(user);
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        UserDto userDto = userService.getById(user.getId());

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void getByIdWrongId() {
        when(userRepository.save(any()))
                .thenReturn(user);
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        userService.delete(user.getId());
        assertEquals(0, userRepository.findAll().size());

    }

}