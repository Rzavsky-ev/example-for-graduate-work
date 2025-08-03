package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;

@RestController
@RequestMapping("/users")
@Tag(name = "Пользователи", description = "Управление профилем пользователя")
public class UserController {

    @Operation(
            summary = "Получить данные текущего пользователя",
            description = "Возвращает информацию об авторизованном пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные пользователя",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован",
                            content = @Content()
                    )
            }
    )
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUser() {
        return new UserDto();
    }

    @Operation(
            summary = "Обновление пароля",
            description = "Изменяет пароль текущего пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пароль успешно изменен",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные данные для смены пароля",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Действие запрещено",
                            content = @Content()
                    )
            }
    )
    @PostMapping("/set_password")
    @ResponseStatus(HttpStatus.OK)
    public void setPassword(@RequestBody NewPasswordDto newPasswordDto) {
    }

    @Operation(
            summary = "Обновить данные пользователя",
            description = "Изменяет информацию о текущем пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные успешно обновлены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateUserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные данные пользователя",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован",
                            content = @Content()
                    )
            }
    )
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UpdateUserDto updateUser(@RequestBody UpdateUserDto updateUserDto) {
        return new UpdateUserDto();
    }

    @Operation(
            summary = "Обновить аватар пользователя",
            description = "Загружает новое изображение для аватара пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Аватар успешно обновлен",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный файл изображения",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не авторизован",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Действие запрещено",
                            content = @Content()
                    )
            }
    )
    @PatchMapping("/me/image")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserImage(@RequestParam("image") MultipartFile image) {
    }
}