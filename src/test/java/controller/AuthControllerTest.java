package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.HomeworkApplication;
import ru.skypro.homework.dto.LoginDto;
import ru.skypro.homework.dto.RegisterUserDto;
import ru.skypro.homework.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HomeworkApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @DisplayName("Успешная аутентификация пользователя")
    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() throws Exception {

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user@example.com");
        loginDto.setPassword("wrong-password");
        String expectedToken = "test-token";

        when(authService.login(loginDto.getUsername(), loginDto.getPassword()))
                .thenReturn(expectedToken);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));
    }

    @DisplayName("Аутентификация с неверными учетными данными")
    @Test
    void loginShouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {

        LoginDto loginDto = new LoginDto();

        loginDto.setUsername("user@example.com");
        loginDto.setPassword("wrong-password");

        when(authService.login(loginDto.getUsername(), loginDto.getPassword()))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Успешная регистрация нового пользователя")
    @Test
    void registerShouldReturnCreatedWhenRegistrationIsSuccessful() throws Exception {

        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("newuser@example.com");
        registerDto.setPassword("password");
        registerDto.setFirstName("Иван");
        registerDto.setLastName("Иванов");
        registerDto.setPhone("+79991234567");

        when(authService.register(any(RegisterUserDto.class))).thenReturn(true);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isCreated());
    }

    @DisplayName("Регистрация с некорректными данными")
    @Test
    void registerShouldReturnBadRequestWhenRegistrationFails() throws Exception {

        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("invalid-email");
        registerDto.setPassword("short");

        when(authService.register(any(RegisterUserDto.class))).thenReturn(false);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Регистрация с неполными данными")
    @Test
    void registerShouldReturnBadRequestWhenDataIsIncomplete() throws Exception {

        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("user@example.com");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest());
    }
}

