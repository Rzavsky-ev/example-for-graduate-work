package ru.skypro.homework.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterUserDto;

@Tag(
        name = "Authentication Service",
        description = "Обрабатывает логин и регистрацию пользователей"
)
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsManager manager;
    private final PasswordEncoder encoder;

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Проверяет логин и пароль пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешная аутентификация"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неверные учетные данные"
                    )
            }
    )
    public boolean login(String userName, String password) {
        if (!manager.userExists(userName)) {
            return false;
        }
        UserDetails userDetails = manager.loadUserByUsername(userName);
        return encoder.matches(password, userDetails.getPassword());
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя в системе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь успешно зарегистрирован"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Пользователь с таким именем уже существует"
                    )
            }
    )
    public boolean register(RegisterUserDto register) {
        if (manager.userExists(register.getUsername())) {
            return false;
        }
        manager.createUser(
                User.builder()
                        .passwordEncoder(this.encoder::encode)
                        .password(register.getPassword())
                        .username(register.getUsername())
                        .roles(register.getRole().name())
                        .build());
        return true;
    }
}
