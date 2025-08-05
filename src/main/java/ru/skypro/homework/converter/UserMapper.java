package ru.skypro.homework.converter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.RegisterUserDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;

@Tag(
        name = "UserMapper",
        description = "Конвертер для преобразования между сущностью User и DTO объектами"
)
@Component
public class UserMapper {

    @Operation(
            summary = "Создать User из RegisterUserDto",
            description = "Преобразует DTO регистрации пользователя в сущность User",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное преобразование",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные данные для регистрации"
                    )
            }
    )
    public User registerDtoToUser(RegisterUserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setRole(Role.USER);
        return user;
    }

    @Operation(
            summary = "Конвертировать User в UserDto",
            description = "Преобразует сущность User в DTO для отображения информации о пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное преобразование",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    )
            }
    )
    public UserDto userToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setImage(user.getImagePath());
        return dto;
    }

    @Operation(
            summary = "Обновить User из UpdateUserDto",
            description = "Обновляет данные пользователя на основе DTO обновления. Проверяет формат телефона (+7 XXX XXX-XX-XX)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное обновление"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверный формат телефона",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = IllegalArgumentException.class)
                            )
                    )
            }
    )
    public void updateUserFromDto(UpdateUserDto dto, User user) {
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getPhone() != null) {
            if (dto.getPhone().matches("\\+7\\s?\\(?\\d{3}\\)?\\s?\\d{3}-?\\d{2}-?\\d{2}")) {
                user.setPhone(dto.getPhone());
            } else {
                throw new IllegalArgumentException("Неверный формат телефона");
            }
        }
    }
}

