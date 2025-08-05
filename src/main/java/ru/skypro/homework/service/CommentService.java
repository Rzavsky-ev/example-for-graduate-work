package ru.skypro.homework.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skypro.homework.converter.CommentMapper;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;

@Tag(
        name = "Comment Service",
        description = "Обрабатывает бизнес-логику, связанную с комментариями"
)
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    @Operation(
            summary = "Преобразовать Comment в CommentDto",
            description = "Конвертирует сущность комментария в DTO для отображения",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное преобразование",
                            content = @Content(
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    )
            }
    )
    public CommentDto mapToCommentDto(Comment comment) {
        return commentMapper.commentToCommentDto(comment);
    }

    @Operation(
            summary = "Создать Comment из DTO",
            description = "Создает новую сущность комментария на основе DTO, автора и объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий успешно создан",
                            content = @Content(
                                    schema = @Schema(implementation = Comment.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные комментария"
                    )
            }
    )
    public Comment mapToComment(CreateOrUpdateCommentDto dto, User author, Ad ad) {
        Comment comment = commentMapper.createCommentDtoToComment(dto);
        comment.setAuthor(author);
        comment.setAd(ad);
        return comment;
    }

    @Operation(
            summary = "Обновить Comment из DTO",
            description = "Обновляет существующий комментарий на основе данных из DTO",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Комментарий успешно обновлен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные для обновления"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Комментарий не найден"
                    )
            }
    )
    public void updateCommentFromDto(CreateOrUpdateCommentDto dto, Comment comment) {
        if (dto.getText() != null && !dto.getText().isBlank()) {
            comment.setText(dto.getText());
        }
    }
}