package ru.practicum.shareit.requests.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private long id;
    @NotBlank
    private String description;
    private User requester;
    private LocalDateTime created;
}