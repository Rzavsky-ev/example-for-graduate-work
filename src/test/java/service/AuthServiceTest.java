package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.RegisterUserDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AuthService;
import ru.skypro.homework.utils.JwtUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @DisplayName("Успешная аутентификация пользователя")
    @Test
    void loginShouldReturnTokenCredentialsAreValid() {

        String username = "testUser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        String expectedToken = "testToken";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(jwtUtils.generateToken(username)).thenReturn(expectedToken);

        String actualToken = authService.login(username, password);

        assertEquals(expectedToken, actualToken);
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
        verify(jwtUtils).generateToken(username);
    }

    @DisplayName("Аутентификация должна выбрасывать исключение при неверном пароле")
    @Test
    void loginShouldThrowExceptionPasswordIsInvalid() {

        String username = "testUser";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword";

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(username, password));
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @DisplayName("Аутентификация должна выбрасывать исключение при отсутствии пользователя")
    @Test
    void loginShouldThrowExceptionUserNotFound() {

        String username = "nonExistentUser";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(username, password));
        verify(userRepository).findByUsername(username);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtUtils);
    }

    @DisplayName("Успешная регистрация нового пользователя")
    @Test
    void registerShouldReturnTrueUserDoesNotExist() {

        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("newUser");
        registerUserDto.setPassword("password");
        registerUserDto.setFirstName("John");
        registerUserDto.setLastName("Doe");
        registerUserDto.setPhone("+79991234567");
        registerUserDto.setRole(Role.USER);

        when(userRepository.existsByUsername(registerUserDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerUserDto.getPassword())).thenReturn("encodedPassword");

        boolean result = authService.register(registerUserDto);

        assertTrue(result);
        verify(userRepository).existsByUsername(registerUserDto.getUsername());
        verify(passwordEncoder).encode(registerUserDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @DisplayName("Регистрация должна вернуть false при существующем пользователе")
    @Test
    void registerShouldReturnFalseUserAlreadyExists() {

        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setUsername("existingUser");
        registerUserDto.setPassword("password");
        registerUserDto.setFirstName("John");
        registerUserDto.setLastName("Doe");
        registerUserDto.setPhone("+79991234567");
        registerUserDto.setRole(Role.USER);

        when(userRepository.existsByUsername(registerUserDto.getUsername())).thenReturn(true);

        boolean result = authService.register(registerUserDto);

        assertFalse(result);
        verify(userRepository).existsByUsername(registerUserDto.getUsername());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
    }
}