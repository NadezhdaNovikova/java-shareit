package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findItemsByOwnerId(long userId, Pageable pageRequest);

    @Query("select i from Item i " +
            "where i.available = true and (upper(i.name) like upper(concat('%', ?1, '%')) " +
                "or upper(i.description) like upper(concat('%', ?1, '%')))")
    List<Item> searchItemsByText(String text, Pageable pageRequest);

    List<Item> findAllByRequestId(long id);
}