package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads/{id}/comments")
@Tag(name = "Комментарии", description = "Управление комментариями к объявлениям")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Получить все комментарии объявления",
            description = "Возвращает список всех комментариев для указанного объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарии найдены",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = CommentDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено"
                    )
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@Parameter(description = "ID объявления", required = true) @PathVariable Integer id) {
        return commentService.getCommentsByAdId(id);
    }

    @Operation(
            summary = "Добавить комментарий",
            description = "Добавляет новый комментарий к указанному объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Комментарий создан",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено"
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @Parameter(description = "ID объявления", required = true) @PathVariable Integer id,
            @RequestBody @Valid CreateOrUpdateCommentDto commentDto,
            Authentication authentication
    ) {
        return commentService.addComment(id, commentDto, authentication);
    }

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет комментарий по его ID из указанного объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Комментарий удален"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав для удаления комментария"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление или комментарий не найдены"
                    )
            }
    )
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @Parameter(description = "ID объявления", required = true) @PathVariable Integer id,
            @Parameter(description = "ID комментария", required = true) @PathVariable Integer commentId
    ) {
        commentService.deleteComment(id, commentId);
    }

    @Operation(
            summary = "Обновить комментарий",
            description = "Обновляет текст указанного комментария",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав для обновления комментария"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление или комментарий не найдены"
                    )
            }
    )
    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(
            @Parameter(description = "ID объявления", required = true) @PathVariable Integer id,
            @Parameter(description = "ID комментария", required = true) @PathVariable Integer commentId,
            @RequestBody @Valid CreateOrUpdateCommentDto updatedComment
    ) {
        return commentService.updateComment(id, commentId, updatedComment);
    }
}