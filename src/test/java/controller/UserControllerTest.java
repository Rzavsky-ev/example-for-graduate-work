package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.HomeworkApplication;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HomeworkApplication.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @DisplayName("Получение текущего пользователя - должен вернуть данные пользователя")
    @Test
    @WithMockUser
    void getCurrentUserShouldReturnUserData() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setEmail("user@example.com");
        userDto.setFirstName("Иван");
        userDto.setLastName("Иванов");

        when(userService.getCurrentUser(any(Authentication.class))).thenReturn(userDto);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.firstName").value("Иван"))
                .andExpect(jsonPath("$.lastName").value("Иванов"));

        verify(userService, times(1)).getCurrentUser(any(Authentication.class));
    }

    @DisplayName("Обновление пароля - должен успешно изменить пароль")
    @Test
    @WithMockUser
    void updatePasswordShouldChangePasswordSuccessfully() throws Exception {

        NewPasswordDto newPasswordDto = new NewPasswordDto();
        newPasswordDto.setCurrentPassword("oldPassword");
        newPasswordDto.setNewPassword("newPassword");

        mockMvc.perform(post("/users/set_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPasswordDto)))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .updatePassword(any(NewPasswordDto.class), any(Authentication.class));
    }

    @DisplayName("Обновление профиля - должен вернуть обновленные данные")
    @Test
    @WithMockUser
    void updateUserShouldReturnUpdatedUserData() throws Exception {

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("Петр");
        updateUserDto.setLastName("Петров");
        updateUserDto.setPhone("+79991234567");

        UpdateUserDto updatedUser = new UpdateUserDto();
        updatedUser.setFirstName("Петр");
        updatedUser.setLastName("Петров");
        updatedUser.setPhone("+79991234567");

        when(userService.updateUser(any(UpdateUserDto.class), any(Authentication.class)))
                .thenReturn(updatedUser);

        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Петр"))
                .andExpect(jsonPath("$.lastName").value("Петров"))
                .andExpect(jsonPath("$.phone").value("+79991234567"));

        verify(userService, times(1))
                .updateUser(any(UpdateUserDto.class), any(Authentication.class));
    }

    @DisplayName("Загрузка изображения пользователя - должен вернуть изображение")
    @Test
    @WithMockUser
    void downloadUserImageShouldReturnImage() throws Exception {

        byte[] imageBytes = "test image content".getBytes();
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                imageBytes
        );

        when(userService.downloadUserImage(any(MultipartFile.class), any(Authentication.class)))
                .thenReturn(imageBytes);

        mockMvc.perform(multipart("/users/me/image")
                        .file(imageFile)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(content().bytes(imageBytes));

        verify(userService, times(1))
                .downloadUserImage(any(MultipartFile.class), any(Authentication.class));
    }

    @DisplayName("Получение текущего пользователя без аутентификации - должен вернуть 401")
    @Test
    void getCurrentUserWithoutAuthShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }
}