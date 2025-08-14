package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

    @Operation(summary = "Загрузить изображение для объявления",
            responses = @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE)))
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] downloadUserImage(
            @RequestParam("image") MultipartFile image,
            Authentication authentication) throws IOException {
        return userService.downloadUserImage(image, authentication);
    }
}