package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для регистрации нового пользователя")
public class RegisterUserDto {
    @Schema(
            description = "Логин пользователя (email)",
            example = "user@example.com",
            required = true,
            pattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
    )
    private String username;

    @Schema(
            description = "Пароль пользователя",
            example = "MySecurePassword123!",
            minLength = 8,
            maxLength = 64,
            required = true,
            pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+]).{8,}$"
    )
    private String password;

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
            description = "Контактный телефон",
            example = "+79991234567",
            pattern = "^\\+?[0-9]{10,15}$"
    )
    private String phone;

    @Schema(
            description = "Роль пользователя",
            example = "USER",
            allowableValues = {"USER", "ADMIN"},
            defaultValue = "USER"
    )
    private Role role;
}