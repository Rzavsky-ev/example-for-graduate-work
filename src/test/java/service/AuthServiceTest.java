package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import ru.skypro.homework.dto.RegisterUserDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.service.AuthService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;


    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("Логин: успешная аутентификация при верных учетных данных")
    @Test
    void loginShouldReturnTrueCredentialsAreValid() {

        String username = "user@example.com";
        String password = "password";
        String encodedPassword = "encodedPassword";

        UserDetails userDetails = User.builder()
                .username(username)
                .password(encodedPassword)
                .roles("USER")
                .build();

        when(userDetailsManager.userExists(username)).thenReturn(true);
        when(userDetailsManager.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        boolean result = authService.login(username, password);

        assertTrue(result);
        verify(userDetailsManager).userExists(username);
        verify(userDetailsManager).loadUserByUsername(username);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @DisplayName("Логин: неудача при попытке входа несуществующего пользователя")
    @Test
    void loginShouldReturnFalseUserDoesNotExist() {

        String username = "nonexistent@example.com";
        String password = "password";

        when(userDetailsManager.userExists(username)).thenReturn(false);

        boolean result = authService.login(username, password);

        assertFalse(result);
        verify(userDetailsManager).userExists(username);
        verify(userDetailsManager, never()).loadUserByUsername(any());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @DisplayName("Логин: неудача при неверном пароле")
    @Test
    void loginShouldReturnFalsePasswordIsInvalid() {

        String username = "user@example.com";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword";

        UserDetails userDetails = User.builder()
                .username(username)
                .password(encodedPassword)
                .roles("USER")
                .build();

        when(userDetailsManager.userExists(username)).thenReturn(true);
        when(userDetailsManager.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        boolean result = authService.login(username, password);

        assertFalse(result);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @DisplayName("Регистрация: успешное создание нового пользователя")
    @Test
    void registerShouldReturnTrueUserDoesNotExist() {

        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("newuser@example.com");
        registerDto.setPassword("newPassword");
        registerDto.setRole(Role.USER);

        when(userDetailsManager.userExists(registerDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");

        boolean result = authService.register(registerDto);

        assertTrue(result);
        verify(userDetailsManager).createUser(any(UserDetails.class));
        verify(passwordEncoder).encode(registerDto.getPassword());
    }

    @DisplayName("Регистрация: неудача при попытке регистрации существующего пользователя")
    @Test
    void registerReturnFalseUserAlreadyExists() {

        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("existing@example.com");
        registerDto.setPassword("password");
        registerDto.setRole(Role.USER);

        when(userDetailsManager.userExists(registerDto.getUsername())).thenReturn(true);

        boolean result = authService.register(registerDto);

        assertFalse(result);
        verify(userDetailsManager, never()).createUser(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @DisplayName("Регистрация: проверка корректности данных создаваемого пользователя")
    @Test
    void registerCreateUserWithCorrectDetails() {

        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("test@example.com");
        registerDto.setPassword("testPass");
        registerDto.setRole(Role.ADMIN);

        String encodedPassword = "encodedTestPass";

        when(userDetailsManager.userExists(registerDto.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn(encodedPassword);

        authService.register(registerDto);

        verify(userDetailsManager).createUser(argThat(userDetails ->
                userDetails.getUsername().equals(registerDto.getUsername()) &&
                        userDetails.getPassword().equals(encodedPassword) &&
                        userDetails.getAuthorities().stream()
                                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + registerDto.getRole().name()))
        ));
    }
}