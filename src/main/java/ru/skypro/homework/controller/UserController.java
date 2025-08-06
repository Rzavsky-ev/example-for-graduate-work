package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Управление профилем пользователя")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Получить текущего пользователя",
            description = "Возвращает информацию о текущем аутентифицированном пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Информация о пользователе получена",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация"
                    )
            }
    )
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getCurrentUser(Authentication authentication) {
        return userService.getCurrentUser(authentication);
    }

    @Operation(
            summary = "Обновить пароль",
            description = "Изменяет пароль текущего пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пароль успешно изменен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный запрос (некорректные данные)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен (неверный текущий пароль)"
                    )
            }
    )
    @PostMapping("/set_password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(
            @RequestBody @Valid NewPasswordDto newPasswordDto,
            Authentication authentication) {
        userService.updatePassword(newPasswordDto, authentication);
    }

    @Operation(
            summary = "Обновить профиль",
            description = "Обновляет информацию о текущем пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Профиль успешно обновлен",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UpdateUserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный запрос (некорректные данные)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация"
                    )
            }
    )
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UpdateUserDto updateUser(
            @RequestBody @Valid UpdateUserDto updateUserDto,
            Authentication authentication) {
        return userService.updateUser(updateUserDto, authentication);
    }

    @Operation(
            summary = "Обновить аватар",
            description = "Загружает новый аватар для текущего пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Аватар успешно обновлен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный запрос (неподдерживаемый формат изображения)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Требуется аутентификация"
                    ),
                    @ApiResponse(
                            responseCode = "413",
                            description = "Размер файла слишком большой"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "string", format = "binary"),
                            encoding = @Encoding(name = "image", contentType = "image/jpeg, image/png")
                    )
            )
    )
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateUserImage(
            @Parameter(description = "Файл изображения (JPEG/PNG)", required = true)
            @RequestParam MultipartFile image,
            Authentication authentication) throws IOException {
        userService.updateUserImage(image, authentication);
    }

    @Operation(
            summary = "Получить аватар пользователя",
            description = "Возвращает аватар пользователя по его username",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Аватар получен",
                            content = @Content(mediaType = "image/jpeg, image/png")
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь или аватар не найдены"
                    )
            }
    )
    @GetMapping(value = "/{username}/image", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public byte[] getUserImage(
            @Parameter(description = "Username пользователя", required = true)
            @PathVariable String username) throws IOException {
        return userService.getUserImage(username);
    }
}