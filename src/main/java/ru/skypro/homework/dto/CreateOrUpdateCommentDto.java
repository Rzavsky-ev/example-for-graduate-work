package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@Schema(description = "DTO для создания или обновления комментария")
public class CreateOrUpdateCommentDto {

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(min = 8, max = 64, message = "Текст комментария должен содержать от 8 до 64 символов")
    @Schema(
            description = "Текст комментария",
            example = "Это очень полезный комментарий, спасибо!",
            minLength = 8,
            maxLength = 64,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String text;
}
