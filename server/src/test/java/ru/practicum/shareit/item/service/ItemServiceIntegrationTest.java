package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;

    @Test
    void add() {
        User user = new User(1L, "User", "testitemadd@email.ru");
        Item item1 = new Item(1L, "ItemCreateTest", "description", true, user, null);
        ItemDto itemDto = ItemMapper.toItemDto(item1);
        itemDto.setId(0L);

        em.createNativeQuery("insert into users (user_name, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User owner = query1
                .setParameter("email", user.getEmail())
                .getSingleResult();

        itemService.add(owner.getId(), itemDto);

        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut = query2
                .setParameter("name", itemDto.getName())
                .getSingleResult();

        assertThat(itemOut.getId(), notNullValue());
        assertThat(itemOut.getName(), equalTo(itemDto.getName()));
        assertThat(itemOut.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void getAllItemsByOwner() {

        User user = new User(2L, "UserGetAllItems", "user@email.ru");

        em.createNativeQuery("insert into users (user_name, email) values (?, ?)")
                .setParameter(1, user.getName())
                .setParameter(2, user.getEmail())
                .executeUpdate();

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User owner = query1
                .setParameter("email", user.getEmail())
                .getSingleResult();

        Item item1 = new Item(2L, "Item1ForOwner", "Description", true, owner, null);
        Item item2 = new Item(3L, "Item2ForOwner", "Description2", true, owner, null);


        em.createNativeQuery("insert into items (item_name, description, is_available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item1.getName())
                .setParameter(2, item1.getDescription())
                .setParameter(3, item1.getAvailable())
                .setParameter(4, owner.getId())
                .executeUpdate();

        em.createNativeQuery("insert into items (item_name, description, is_available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item2.getName())
                .setParameter(2, item2.getDescription())
                .setParameter(3, item2.getAvailable())
                .setParameter(4, owner.getId())
                .executeUpdate();

        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut1 = query2
                .setParameter("name", ItemMapper.toItemDto(item1).getName())
                .getSingleResult();

        TypedQuery<Item> query3 = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut2 = query3
                .setParameter("name", ItemMapper.toItemDto(item2).getName())
                .getSingleResult();

        ItemResponseDto itemResponseDto1 = ItemMapper.toItemResponseDto(itemOut1, null,
                null, new ArrayList<>());
        ItemResponseDto itemResponseDto2 = ItemMapper.toItemResponseDto(itemOut2, null,
                null, new ArrayList<>());
        assertThat(itemService.getAllItemsByOwner(owner.getId(), Pageable.ofSize(10)),
                equalTo(List.of(itemResponseDto1, itemResponseDto2)));
    }
}