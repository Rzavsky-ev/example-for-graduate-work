package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для обновления данных пользователя")
public class UpdateUserDto {
    @Schema(
            description = "Имя пользователя",
            example = "Иван",
            minLength = 2,
            maxLength = 16,
            required = true
    )
    private String firstName;

    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов",
            minLength = 2,
            maxLength = 16,
            required = true
    )
    private String lastName;

    @Schema(
            description = "Контактный телефон пользователя",
            example = "+79991234567",
            pattern = "^\\+?[0-9]{10,15}$",
            required = true
    )
    private String phone;
}