package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingStorage extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdAAndItemId(long userId, long itemId, LocalDateTime now);

    boolean existsByItem_IdAndBooker_IdAndEndBefore(long itemId, long userId, LocalDateTime now);
}