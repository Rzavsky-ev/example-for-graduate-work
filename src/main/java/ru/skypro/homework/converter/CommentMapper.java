package ru.skypro.homework.converter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.model.Comment;

@Tag(
        name = "CommentMapper",
        description = "Конвертер для преобразования между сущностью Comment и DTO объектами"
)
@Component
public class CommentMapper {

    @Operation(
            summary = "Создать Comment из CreateOrUpdateCommentDto",
            description = "Преобразует DTO для создания/обновления комментария в сущность Comment",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное преобразование",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Comment.class)
                            )
                    )
            }
    )
    public Comment createCommentDtoToComment(CreateOrUpdateCommentDto dto) {
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setCreatedAt(System.currentTimeMillis());
        return comment;
    }

    @Operation(
            summary = "Конвертировать Comment в CommentDto",
            description = "Преобразует сущность Comment в DTO для отображения комментария",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное преобразование",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentDto.class)
                            )
                    )
            }
    )
    public CommentDto commentToCommentDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setPk(comment.getId());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());

        if (comment.getAuthor() != null) {
            dto.setAuthor(comment.getAuthor().getId());
            dto.setAuthorFirstName(comment.getAuthor().getFirstName());
            dto.setAuthorImage(comment.getAuthor().getImagePath());
        }

        return dto;
    }
}
