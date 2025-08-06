package ru.skypro.homework.repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.User;

import java.util.Optional;

@Tag(
        name = "User Repository",
        description = "Интерфейс для выполнения операций с пользователями"
)
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Operation(
            summary = "Найти пользователя по имени",
            description = "Возвращает пользователя с указанным именем пользователя",
            hidden = true // Скрываем из документации API, так как это внутренний метод репозитория
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь найден",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = User.class)
            )
    )
    Optional<User> findByUsername(String username);
}