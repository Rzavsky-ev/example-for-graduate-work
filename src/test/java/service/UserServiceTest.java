package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.converter.UserMapper;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.ImageService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ImageService imageService;

    @Mock
    private Authentication authentication;

    @Mock
    private MultipartFile multipartFile;

    private User testUser;
    private final String username = "test@example.com";
    private final String encodedPassword = "encodedPassword";
    private final String firstName = "John";
    private final String lastName = "Doe";
    private final String phone = "+7 (123) 456-78-90";
    private final String imagePath = "users/image.jpg";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername(username);
        testUser.setPassword(encodedPassword);
        testUser.setFirstName(firstName);
        testUser.setLastName(lastName);
        testUser.setPhone(phone);
        testUser.setImagePath(imagePath);
    }

    @DisplayName("Получение текущего пользователя - успешное получение DTO при существующем пользователе")
    @Test
    void getCurrentUserShouldReturnUserDtoUserExists() {

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        UserDto expectedDto = new UserDto();
        expectedDto.setId(1);
        expectedDto.setEmail(username);
        expectedDto.setFirstName(firstName);
        expectedDto.setLastName(lastName);
        expectedDto.setPhone(phone);
        expectedDto.setImage("/" + imagePath);
        when(userMapper.userToUserDto(testUser)).thenReturn(expectedDto);

        UserDto result = userService.getCurrentUser(authentication);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(userRepository).findByUsername(username);
        verify(userMapper).userToUserDto(testUser);
    }

    @DisplayName("Получение текущего пользователя - исключение при отсутствии пользователя")
    @Test
    void getCurrentUserShouldThrowExceptionUserNotFound() {

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getCurrentUser(authentication));
        verify(userRepository).findByUsername(username);
    }

    @DisplayName("Обновление пароля - успешное обновление при правильном текущем пароле")
    @Test
    void updatePasswordShouldUpdatePasswordCurrentPasswordIsCorrect() {

        NewPasswordDto newPasswordDto = new NewPasswordDto();
        String password = "password";
        newPasswordDto.setCurrentPassword(password);
        newPasswordDto.setNewPassword("newPassword");

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        userService.updatePassword(newPasswordDto, authentication);

        verify(passwordEncoder).matches(password, encodedPassword);
        verify(passwordEncoder).encode("newPassword");
        assertEquals("newEncodedPassword", testUser.getPassword());
        verify(userRepository).save(testUser);
    }

    @DisplayName("Обновление пароля - исключение при неверном текущем пароле")
    @Test
    void updatePasswordShouldThrowExceptionCurrentPasswordIsIncorrect() {

        NewPasswordDto newPasswordDto = new NewPasswordDto();
        newPasswordDto.setCurrentPassword("wrongPassword");
        newPasswordDto.setNewPassword("newPassword");

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", encodedPassword)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.updatePassword(newPasswordDto, authentication));
        verify(passwordEncoder).matches("wrongPassword", encodedPassword);
        verify(userRepository, never()).save(any());
    }

    @DisplayName("Обновление данных пользователя - успешное обновление при валидных данных")
    @Test
    void updateUserShouldUpdateUserDataValidDataProvided() {

        UpdateUserDto updateUserDto = new UpdateUserDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(userRepository.save(testUser)).thenReturn(testUser);

        UpdateUserDto result = userService.updateUser(updateUserDto, authentication);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("+7 (123) 456-78-90", result.getPhone());
        verify(userRepository).save(testUser);
    }

    @DisplayName("Обновление аватара пользователя - успешное обновление при валидном изображении")
    @Test
    void updateUserImageShouldUpdateUserImageValidImageProvided() throws IOException {

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(imageService.saveUserImage(multipartFile)).thenReturn("users/newImage.jpg");
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.updateUserImage(multipartFile, authentication);

        verify(imageService).deleteImage(imagePath);
        verify(imageService).saveUserImage(multipartFile);
        assertEquals("users/newImage.jpg", testUser.getImagePath());
        verify(userRepository).save(testUser);
    }

    @DisplayName("Обновление аватара пользователя - не удаляет старый аватар при его отсутствии")
    @Test
    void updateUserImageShouldNotDeleteOldImageNoExistingImage() throws IOException {

        testUser.setImagePath(null);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(imageService.saveUserImage(multipartFile)).thenReturn("users/newImage.jpg");
        when(userRepository.save(testUser)).thenReturn(testUser);

        userService.updateUserImage(multipartFile, authentication);

        verify(imageService, never()).deleteImage(any());
        verify(imageService).saveUserImage(multipartFile);
        assertEquals("users/newImage.jpg", testUser.getImagePath());
        verify(userRepository).save(testUser);
    }

    @DisplayName("Получение аватара пользователя - успешное получение байтов изображения")
    @Test
    void getUserImageShouldReturnImageBytesImageExists() throws IOException {

        byte[] expectedImage = new byte[]{1, 2, 3};
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(imageService.getImage(imagePath)).thenReturn(expectedImage);

        byte[] result = userService.getUserImage(username);

        assertArrayEquals(expectedImage, result);
        verify(userRepository).findByUsername(username);
        verify(imageService).getImage(imagePath);
    }

    @DisplayName("Получение аватара пользователя - исключение при отсутствии пользователя")
    @Test
    void getUserImageShouldThrowExceptionUserNotFound() throws IOException {

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUserImage(username));
        verify(userRepository).findByUsername(username);
        verify(imageService, never()).getImage(any());
    }

    @DisplayName("Получение аватара пользователя - исключение при отсутствии изображения")
    @Test
    void getUserImageShouldThrowExceptionImageNotFound() throws IOException {

        testUser.setImagePath(null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        assertThrows(NoSuchElementException.class, () -> userService.getUserImage(username));
        verify(userRepository).findByUsername(username);
        verify(imageService, never()).getImage(any());
    }

    @DisplayName("Преобразование User в UserDto - успешное преобразование")
    @Test
    void mapToUserDtoShouldReturnUserDto() {

        UserDto expectedDto = new UserDto();
        when(userMapper.userToUserDto(testUser)).thenReturn(expectedDto);

        UserDto result = userService.mapToUserDto(testUser);

        assertEquals(expectedDto, result);
        verify(userMapper).userToUserDto(testUser);
    }

    @DisplayName("Преобразование User в UserDto - успешное преобразование")
    @Test
    void mapToUserShouldReturnUser() {

        RegisterUserDto registerUserDto = new RegisterUserDto();
        when(userMapper.registerDtoToUser(registerUserDto)).thenReturn(testUser);

        User result = userService.mapToUser(registerUserDto);

        assertEquals(testUser, result);
        verify(userMapper).registerDtoToUser(registerUserDto);
    }

    @DisplayName("Обновление пользователя из DTO - успешное обновление данных")
    @Test
    void updateUserFromDtoShouldUpdateUser() {

        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("NewName");
        updateUserDto.setLastName("NewLastName");
        updateUserDto.setPhone("+7 (999) 999-99-99");

        userService.updateUserFromDto(updateUserDto, testUser);

        verify(userMapper).updateUserFromDto(updateUserDto, testUser);
    }
}
