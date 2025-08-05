package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@Schema(description = "DTO для создания или обновления объявления")
public class CreateOrUpdateAdDto {

    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 4, max = 32, message = "Заголовок должен содержать от 4 до 32 символов")
    @Schema(
            description = "Заголовок объявления",
            example = "Продам велосипед",
            minLength = 4,
            maxLength = 32,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String title;

    @NotNull(message = "Цена не может быть null")
    @Min(value = 0, message = "Цена не может быть отрицательной")
    @Max(value = 10_000_000, message = "Цена не может превышать 10 000 000")
    @Schema(
            description = "Цена товара",
            example = "15000",
            minimum = "0",
            maximum = "10000000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer price;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 8, max = 64, message = "Описание должно содержать от 8 до 64 символов")
    @Schema(
            description = "Описание товара",
            example = "Отличный горный велосипед, почти не использовался",
            minLength = 8,
            maxLength = 64,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String description;
}