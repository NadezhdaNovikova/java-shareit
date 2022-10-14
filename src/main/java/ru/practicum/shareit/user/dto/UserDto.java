package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {

    private long id;
    @NotBlank(groups = {Create.class}, message = "Имя должно быть указано!")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Email должен быть указан!")
    @Email(groups = {Create.class, Update.class})
    private String email;
}