package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.HomeworkApplication;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = HomeworkApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final String username = "test@example.com";
    private final UserDto userDto = new UserDto();
    private final UpdateUserDto updateUserDto = new UpdateUserDto();
    private final NewPasswordDto newPasswordDto = new NewPasswordDto();
    private final byte[] testImage = "test image content".getBytes();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userDto.setId(1);
        userDto.setEmail(username);
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setPhone("+79998887766");

        updateUserDto.setFirstName("Updated");
        updateUserDto.setLastName("User");
        updateUserDto.setPhone("+79998887766");

        newPasswordDto.setCurrentPassword("oldPassword");
        newPasswordDto.setNewPassword("newPassword");
    }

    @DisplayName("Получение текущего пользователя - должен вернуть UserDto")
    @Test
    @WithMockUser(username = username)
    void getCurrentUserShouldReturnUserDto() throws Exception {
        when(userService.getCurrentUser(any(Authentication.class))).thenReturn(userDto);

        mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
                .andExpect(jsonPath("$.phone").value(userDto.getPhone()));

        verify(userService, times(1)).getCurrentUser(any(Authentication.class));
    }

    @DisplayName("Получение текущего пользователя без аутентификации - должен вернуть 401")
    @Test
    void getCurrentUserUnauthorizedShouldReturn401() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Обновление пароля - должен вернуть 200 OK")
    @Test
    @WithMockUser(username = username)
    void updatePasswordShouldReturnOk() throws Exception {
        doNothing().when(userService).updatePassword(any(NewPasswordDto.class), any(Authentication.class));

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPasswordDto)))
                .andExpect(status().isOk());

        verify(userService, times(1)).updatePassword(any(NewPasswordDto.class), any(Authentication.class));
    }

    @DisplayName("Обновление данных пользователя - должен вернуть обновленные данные")
    @Test
    @WithMockUser(username = username)
    void updateUserShouldReturnUpdatedUserDto() throws Exception {
        when(userService.updateUser(any(UpdateUserDto.class), any(Authentication.class))).thenReturn(updateUserDto);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updateUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(updateUserDto.getLastName()))
                .andExpect(jsonPath("$.phone").value(updateUserDto.getPhone()));

        verify(userService, times(1)).updateUser(any(UpdateUserDto.class), any(Authentication.class));
    }

    @DisplayName("Обновление аватара пользователя - должен вернуть 200 OK")
    @Test
    @WithMockUser(username = username)
    void updateUserImageShouldReturnOk() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes());

        doNothing().when(userService).updateUserImage(any(), any(Authentication.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/users/me/image")
                        .file(image))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUserImage(any(), any(Authentication.class));
    }

    @DisplayName("Получение аватара пользователя - должен вернуть изображение")
    @Test
    @WithMockUser(username = "test@example.com")
    void getUserImageShouldReturnImage() throws Exception {
        when(userService.getUserImage(eq(username))).thenReturn(testImage);

        mockMvc.perform(get("/users/{username}/image", username))
                .andExpect(status().isOk())
                .andExpect(content().bytes(testImage));

        verify(userService, times(1)).getUserImage(username);
    }
}
