package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestInDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    private String description;

    private User requester;

    private LocalDateTime created;
}