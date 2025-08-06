package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterUserDto;

/**
 * Сервис для аутентификации и регистрации пользователей.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    /**
     * Аутентификация пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return true если аутентификация успешна
     */
    public boolean login(String username, String password) {
        if (!userDetailsManager.userExists(username)) {
            return false;
        }
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
        return passwordEncoder.matches(password, userDetails.getPassword());
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param register данные для регистрации
     * @return true если регистрация успешна
     */
    public boolean register(RegisterUserDto register) {
        if (userDetailsManager.userExists(register.getUsername())) {
            return false;
        }

        UserDetails newUser = User.builder()
                .username(register.getUsername())
                .password(passwordEncoder.encode(register.getPassword()))
                .roles(register.getRole().name())
                .build();

        userDetailsManager.createUser(newUser);
        return true;
    }
}
