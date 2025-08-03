package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Расширенная информация об объявлении")
public class ExtendedAdDto {
    @Schema(
            description = "Идентификатор объявления",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long pk;

    @Schema(
            description = "Имя автора объявления",
            example = "Иван",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String authorFirstName;

    @Schema(
            description = "Фамилия автора объявления",
            example = "Иванов",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String authorLastName;

    @Schema(
            description = "Подробное описание товара",
            example = "Отличный ноутбук в идеальном состоянии",
            minLength = 8,
            maxLength = 512
    )
    private String description;

    @Schema(
            description = "Контактный email автора",
            example = "user@example.com",
            pattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
    )
    private String email;

    @Schema(
            description = "Ссылка на изображение объявления",
            example = "/images/ads/1/image.jpg",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String image;

    @Schema(
            description = "Контактный телефон автора",
            example = "+79991234567",
            pattern = "^\\+?[0-9]{10,15}$"
    )
    private String phone;

    @Schema(
            description = "Цена товара",
            example = "15000",
            minimum = "0"
    )
    private Long price;

    @Schema(
            description = "Заголовок объявления",
            example = "Продам ноутбук",
            minLength = 4,
            maxLength = 32,
            required = true
    )
    private String title;
}