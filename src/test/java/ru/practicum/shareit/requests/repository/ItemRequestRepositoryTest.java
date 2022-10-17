package ru.practicum.shareit.requests.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRequestRepositoryTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    ItemRequest request;
    User user1;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        request = itemRequestRepository.save(
                new ItemRequest(1L, "description of request", user1, LocalDateTime.now()));
    }

    @Test
    void findAllByRequesterIdOrderByCreatedDesc() {
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user1.getId());
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
        assertEquals(request.getDescription(), requests.get(0).getDescription());
        assertEquals(request.getRequester().getId(), requests.get(0).getRequester().getId());
        assertEquals(request.getCreated(), requests.get(0).getCreated());
    }

    @Test
    void findAllByOtherUsers() {
        User user2 = userRepository.save(new User(2L, "user2", "user2@mail.ru"));
        List<ItemRequest> requests = itemRequestRepository.findAllByOtherUsers(user2.getId(), Pageable.ofSize(10));
        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(request.getId(), requests.get(0).getId());
        assertEquals(request.getDescription(), requests.get(0).getDescription());
        assertNotEquals(user2.getId(), requests.get(0).getRequester().getId());
        assertNotEquals(user2.getName(), requests.get(0).getRequester().getName());
        assertNotEquals(user2.getEmail(), requests.get(0).getRequester().getEmail());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }
}