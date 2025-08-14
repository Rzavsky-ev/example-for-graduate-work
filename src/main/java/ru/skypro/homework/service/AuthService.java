package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.RegisterUserDto;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.utils.JwtUtils;

/**
 * Сервис для аутентификации и регистрации пользователей.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;



    /**
     * Аутентификация пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return true если аутентификация успешна
     */
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtils.generateToken(username);
        }
        throw new RuntimeException("Invalid password");
    }

    /**
     * Регистрация нового пользователя.
     *
     * @param register данные для регистрации
     * @return true если регистрация успешна
     */
    public boolean register(RegisterUserDto register) {
        if (userRepository.existsByUsername(register.getUsername())) {
            return false;
        }

        User newUser = new User();
        newUser.setUsername(register.getUsername());
        newUser.setPassword(passwordEncoder.encode(register.getPassword()));
        newUser.setFirstName(register.getFirstName());
        newUser.setLastName(register.getLastName());
        newUser.setPhone(register.getPhone());
        newUser.setRole(register.getRole());

        userRepository.save(newUser);
        return true;
    }
}
