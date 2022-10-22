package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {

    private final EntityManager em;
    private final BookingServiceImpl bookingService;

    @Test
    void add() {
        User user1 = new User(1L, "User1", "user1@email.ru");
        User user2 = new User(2L, "User2", "user2@email.ru");
        Item item = new Item(3L, "ItemForBooking", "Description", true, user1, null);

        em.createNativeQuery("insert into users (user_name, email) values (?, ?)")
                .setParameter(1, user1.getName())
                .setParameter(2, user1.getEmail())
                .executeUpdate();

        em.createNativeQuery("insert into users (user_name, email) values (?, ?)")
                .setParameter(1, user2.getName())
                .setParameter(2, user2.getEmail())
                .executeUpdate();

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User owner = query
                .setParameter("email", user1.getEmail())
                .getSingleResult();

        em.createNativeQuery("insert into items (item_name, description, is_available, owner_id) values (?, ?, ?,?)")
                .setParameter(1, item.getName())
                .setParameter(2, item.getDescription())
                .setParameter(3, item.getAvailable())
                .setParameter(4, owner.getId())
                .executeUpdate();

        TypedQuery<User> query1 = em.createQuery("Select u from User u where u.email = :email", User.class);
        User booker = query1
                .setParameter("email", user2.getEmail())
                .getSingleResult();

        TypedQuery<Item> query2 = em.createQuery("Select i from Item i where i.name = :name", Item.class);
        Item itemOut = query2
                .setParameter("name", ItemMapper.toItemDto(item).getName())
                .getSingleResult();

        BookingRequestDto bookingRequestDto = new BookingRequestDto(itemOut.getId(), LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        bookingService.add(booker.getId(), bookingRequestDto);
        TypedQuery<Booking> query3 =
                em.createQuery("Select b from Booking b where b.booker.id = :booker_id", Booking.class);
        Booking booking = query3.setParameter("booker_id", booker.getId()).getResultList().get(0);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(bookingRequestDto.getStart()));
        assertThat(booking.getEnd(), equalTo(bookingRequestDto.getEnd()));
        assertThat(booking.getItem().getId(), equalTo(bookingRequestDto.getItemId()));
    }
}