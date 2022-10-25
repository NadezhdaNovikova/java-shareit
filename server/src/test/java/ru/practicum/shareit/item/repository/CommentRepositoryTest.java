package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    Item item1;
    User user1;
    Comment comment;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        item1 = itemRepository.save(
                new Item(1L, "item1", "description1", true, user1, null));
        comment = commentRepository.save(new Comment(1L, "text comment", item1, user1, LocalDateTime.now()));
    }

    @Test
    void findCommentsByItemId() {
        List<Comment> comments = commentRepository.findCommentsByItemId(item1.getId());
        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals(comment.getId(), comments.get(0).getId());
        assertEquals(comment.getText(), comments.get(0).getText());
        assertEquals(comment.getItem().getId(), comments.get(0).getItem().getId());
        assertEquals(comment.getAuthor().getName(), comments.get(0).getAuthor().getName());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }
}