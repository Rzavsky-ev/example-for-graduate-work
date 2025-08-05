package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для представления данных пользователя")
public class UserDto {
    @Schema(
            description = "Уникальный идентификатор пользователя",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Integer id;

    @Schema(
            description = "Email пользователя (логин)",
            example = "user@example.com",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String email;

    @Schema(
            description = "Имя пользователя",
            example = "Иван",
            minLength = 2,
            maxLength = 16
    )
    private String firstName;

    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов",
            minLength = 2,
            maxLength = 16
    )
    private String lastName;

    @Schema(
            description = "Телефон пользователя",
            example = "+79991234567",
            pattern = "^\\+?[0-9]{10,15}$"
    )
    private String phone;

    @Schema(
            description = "Роль пользователя",
            example = "USER",
            allowableValues = {"USER", "ADMIN"},
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Role role;

    @Schema(
            description = "Ссылка на аватар пользователя",
            example = "/images/users/1/avatar.jpg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String image;
}