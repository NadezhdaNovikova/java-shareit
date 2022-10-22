package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingStateException;
import ru.practicum.shareit.exception.ValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RequiredArgsConstructor
@Controller
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

	private static final String HEADER_USER_ID = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	@PostMapping()
	public ResponseEntity<Object> add(@RequestHeader(HEADER_USER_ID) long userId,
									  @RequestBody @Valid BookItemRequestDto bookingRequestDto) {
		log.info("Добавление нового запроса на бронирование {}", bookingRequestDto);
		if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())) {
			throw new ValidationException("Дата окончания не может быть раньше даты старта!");
		}
		return bookingClient.add(userId, bookingRequestDto);
	}

	@PatchMapping("{bookingId}")
	public ResponseEntity<Object>  approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
												  @PathVariable long bookingId,
												  @RequestParam("approved") boolean isApproved) {
		return bookingClient.approveBooking(userId, bookingId, isApproved);
	}

	@GetMapping("{bookingId}")
	public ResponseEntity<Object>  getById(@RequestHeader(HEADER_USER_ID) long userId,
									   @PathVariable long bookingId) {
		log.info("Получение запроса {}, userId={}", bookingId, userId);
		return bookingClient.getById(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getByUser(@RequestHeader(HEADER_USER_ID) long userId,
											  @RequestParam(value = "state", defaultValue = "ALL") String stateIn,
											  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
											  Integer from,
											  @Positive @RequestParam(name = "size", defaultValue = "10")
											  Integer size) {
		BookingState state = BookingState.from(stateIn)
				.orElseThrow(() -> new BookingStateException("Unknown state: " + stateIn));
		log.info("Получение запросов {} пользователя userId={}, from={}, size={}", stateIn, userId, from, size);
		return bookingClient.getByUser(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getByOwner(@RequestHeader(HEADER_USER_ID) long userId,
											   @RequestParam(value = "state", defaultValue = "ALL") String stateIn,
											   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
											   Integer from,
											   @Positive @RequestParam(name = "size", defaultValue = "10")
											   Integer size)  {
		BookingState state = BookingState.from(stateIn)
				.orElseThrow(() -> new BookingStateException("Unknown state: " + stateIn));
		log.info("Получение запросов по id владельца вещи, userId={}, from={}, size={}, state={}", userId, from, size,
				stateIn);

		return bookingClient.getByOwner(userId, state, from, size);
	}
}