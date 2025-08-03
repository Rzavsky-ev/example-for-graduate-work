package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для работы с объявлениями")
public class AdDto {
    @Schema(
            description = "Идентификатор автора объявления",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long author;

    @Schema(
            description = "Ссылка на изображение объявления",
            example = "/images/ads/1/image.jpg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String image;

    @Schema(
            description = "Идентификатор объявления",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long pk;

    @Schema(
            description = "Цена товара в объявлении",
            example = "5000",
            minimum = "0"
    )
    private Long price;

    @Schema(
            description = "Заголовок объявления",
            example = "Продам ноутбук",
            minLength = 4,
            maxLength = 32
    )
    private String title;
}

