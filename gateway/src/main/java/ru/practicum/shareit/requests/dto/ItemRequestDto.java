package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;

    @NotBlank(groups = {Create.class})
    private String description;

    private LocalDateTime created;
}