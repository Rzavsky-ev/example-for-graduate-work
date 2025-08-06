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
import ru.skypro.homework.model.Comment;

import java.util.Collection;

@Tag(
        name = "Comment Repository",
        description = "Интерфейс для выполнения операций CRUD с комментариями"
)
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Operation(
            summary = "Найти все комментарии объявления",
            description = "Возвращает коллекцию всех комментариев для указанного объявления",
            hidden = true // Скрываем из документации API, так как это внутренний метод репозитория
    )
    @ApiResponse(
            responseCode = "200",
            description = "Комментарии успешно найдены",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = Comment.class))
            ))
    Collection<Comment> findAllByAd(Ad ad);
}