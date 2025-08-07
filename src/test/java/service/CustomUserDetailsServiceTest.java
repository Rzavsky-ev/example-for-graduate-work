package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CustomUserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("Должен успешно загрузить пользователя по имени пользователя")
    @Test
    void shouldLoadUserByUsernameSuccessfully() {

        String username = "testUser";
        String password = "testPass";
        Role role = Role.USER;

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role.name())));

        verify(userRepository, times(1)).findByUsername(username);
    }

    @DisplayName("Должен выбросить исключение, когда пользователь не найден")
    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        String username = "nonExistingUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(username)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
    }

    @DisplayName("Должен корректно преобразовать роль пользователя в GrantedAuthority")
    @Test
    void shouldCorrectlyConvertUserRoleToGrantedAuthority() {

        String username = "adminUser";
        String password = "adminPass";
        Role role = Role.ADMIN;

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));
    }
}