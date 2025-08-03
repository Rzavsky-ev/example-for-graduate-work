package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/ads/{id}/comments")
@Tag(name = "Комментарии", description = "Управление комментариями к объявлениям")
public class CommentController {

    @Operation(
            summary = "Получить все комментарии объявления",
            description = "Возвращает список всех комментариев для указанного объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарии найдены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentDto.class, type = "array")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content()
                    )
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@PathVariable Long id) {
        return List.of();
    }

    @Operation(
            summary = "Добавить комментарий",
            description = "Добавляет новый комментарий к указанному объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Комментарий успешно добавлен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные данные комментария",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content()
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long id,
                                 @RequestBody CommentDto commentDto) {
        return new CommentDto();
    }

    @Operation(
            summary = "Обновить комментарий",
            description = "Обновляет текст указанного комментария",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий успешно обновлен",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные данные комментария",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Запрещено редактировать чужой комментарий",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Комментарий или объявление не найдены",
                            content = @Content()
                    )
            }
    )
    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable("id") Long adId,
                                    @PathVariable Long commentId,
                                    @RequestBody CommentDto commentDto) {
        return new CommentDto();
    }

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет указанный комментарий",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Комментарий успешно удален",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Запрещено удалять чужой комментарий",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Комментарий или объявление не найдены",
                            content = @Content()
                    )
            }
    )
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("id") Long adId,
                              @PathVariable Long commentId) {
    }
}