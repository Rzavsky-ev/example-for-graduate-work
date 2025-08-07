package controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.HomeworkApplication;
import ru.skypro.homework.dto.LoginDto;
import ru.skypro.homework.dto.RegisterUserDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = HomeworkApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @DisplayName("Логин: должен возвращать статус OK при валидных учетных данных")
    @Test
    void loginShouldReturnOkCredentialsAreValid() throws Exception {

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("user@example.com");
        loginDto.setPassword("password123");

        when(authService.login("user@example.com", "password123")).thenReturn(true);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());
    }

    @DisplayName("Логин: должен возвращать статус Unauthorized при неверных учетных данных")
    @Test
    void loginShouldReturnUnauthorizedCredentialsAreInvalid() throws Exception {

        when(authService.login(any(), any())).thenReturn(false);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"wrong@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Регистрация: должен возвращать статус Created при успешной регистрации")
    @Test
    void registerShouldReturnCreatedRegistrationIsSuccessful() throws Exception {

        RegisterUserDto registerDto = new RegisterUserDto();
        registerDto.setUsername("new@example.com");
        registerDto.setPassword("newPassword123");
        registerDto.setFirstName("John");
        registerDto.setLastName("Doe");
        registerDto.setPhone("+79990001122");
        registerDto.setRole(Role.USER);

        when(authService.register(any(RegisterUserDto.class))).thenReturn(true);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"new@example.com\"," +
                                "\"password\":\"newPassword123\"," +
                                "\"firstName\":\"John\"," +
                                "\"lastName\":\"Doe\"," +
                                "\"phone\":\"+79990001122\"," +
                                "\"role\":\"USER\"}"))
                .andExpect(status().isCreated());
    }

    @DisplayName("Регистрация: должен возвращать статус Bad Request при неудачной регистрации")
    @Test
    void registerShouldReturnBadRequestRegistrationFails() throws Exception {

        when(authService.register(any(RegisterUserDto.class))).thenReturn(false);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"invalid@example.com\"," +
                                "\"password\":\"short\"," +
                                "\"firstName\":\"\"," +
                                "\"lastName\":\"\"," +
                                "\"phone\":\"123\"," +
                                "\"role\":\"USER\"}"))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Логин: должен возвращать статус Unauthorized при невалидном теле запроса")
    @Test
    void loginShouldReturnUnauthorizedWhenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\"}"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Регистрация: должен возвращать статус Bad Request при невалидном теле запроса")
    @Test
    void registerShouldReturnBadRequestRequestBodyIsInvalid() throws Exception {

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
