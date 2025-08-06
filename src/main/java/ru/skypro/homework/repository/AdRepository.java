package ru.skypro.homework.repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;

import java.util.Collection;

@Tag(
        name = "Ad Repository",
        description = "Интерфейс для работы с объявлениями в базе данных"
)
@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {

    @Operation(
            summary = "Найти все объявления автора",
            description = "Возвращает коллекцию всех объявлений, созданных указанным пользователем",
            hidden = true
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешно найдены объявления",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = Ad.class))
            ))
    Collection<Ad> findAllByAuthor(User author);
}