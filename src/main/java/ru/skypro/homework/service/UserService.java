package ru.skypro.homework.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skypro.homework.converter.UserMapper;
import ru.skypro.homework.dto.RegisterUserDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;


@Tag(
        name = "User Service",
        description = "Обрабатывает бизнес-логику, связанную с пользователями"
)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    @Operation(
            summary = "Преобразовать User в UserDto",
            description = "Конвертирует сущность пользователя в DTO для безопасного отображения",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное преобразование",
                            content = @Content(
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    )
            }
    )
    public UserDto mapToUserDto(User user) {
        return userMapper.userToUserDto(user);
    }

    @Operation(
            summary = "Создать User из RegisterUserDto",
            description = "Преобразует DTO регистрации в сущность пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно создан",
                            content = @Content(
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные регистрации"
                    )
            }
    )
    public User mapToUser(RegisterUserDto dto) {
        return userMapper.registerDtoToUser(dto);
    }

    @Operation(
            summary = "Обновить User из UpdateUserDto",
            description = "Обновляет данные пользователя на основе DTO",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные пользователя успешно обновлены"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные для обновления",
                            content = @Content(
                                    schema = @Schema(implementation = IllegalArgumentException.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден"
                    )
            }
    )
    public void updateUserFromDto(UpdateUserDto dto, User user) {
        userMapper.updateUserFromDto(dto, user);
    }
}