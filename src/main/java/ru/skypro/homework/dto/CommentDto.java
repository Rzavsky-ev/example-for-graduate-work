package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для работы с комментариями")
public class CommentDto {
    @Schema(
            description = "Идентификатор автора комментария",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long author;

    @Schema(
            description = "Ссылка на аватар автора комментария",
            example = "/images/users/123/avatar.jpg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String authorImage;

    @Schema(
            description = "Имя автора комментария",
            example = "Иван",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String authorFirstName;

    @Schema(
            description = "Дата создания комментария в миллисекундах (Unix timestamp)",
            example = "1678901234567",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long createdAt;

    @Schema(
            description = "Идентификатор комментария",
            example = "456",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long pk;

    @Schema(
            description = "Текст комментария",
            example = "Отличное объявление!",
            minLength = 8,
            maxLength = 512,
            required = true
    )
    private String text;
}