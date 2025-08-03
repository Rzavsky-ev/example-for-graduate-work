package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для смены пароля пользователя")
public class NewPasswordDto {
    @Schema(
            description = "Текущий пароль пользователя",
            example = "oldPassword123",
            minLength = 8,
            maxLength = 64,
            required = true
    )
    private String currentPassword;

    @Schema(
            description = "Новый пароль пользователя",
            example = "newSecurePassword456",
            minLength = 8,
            maxLength = 64,
            required = true,
            pattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
    )
    private String newPassword;
}