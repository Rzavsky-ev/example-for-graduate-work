package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Данные для входа в систему")
public class LoginDto {
    @Schema(
            description = "Логин пользователя",
            example = "user@example.com",
            minLength = 4,
            maxLength = 32,
            required = true
    )
    private String username;

    @Schema(
            description = "Пароль пользователя",
            example = "mySecretPassword123",
            minLength = 8,
            maxLength = 64,
            required = true
    )
    private String password;
}