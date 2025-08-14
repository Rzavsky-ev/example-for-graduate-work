package ru.skypro.homework.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.converter.UserMapper;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterUserDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Сервис для работы с пользователями.
 * Обеспечивает управление пользовательскими данными, включая:
 * - Получение информации о текущем пользователе
 * - Обновление профиля и пароля
 * - Управление аватаром пользователя
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    /**
     * Преобразует сущность User в UserDto.
     *
     * @param user сущность пользователя
     * @return DTO пользователя
     */
    public UserDto mapToUserDto(User user) {
        return userMapper.userToUserDto(user);
    }

    /**
     * Преобразует RegisterUserDto в сущность User.
     *
     * @param dto DTO регистрации пользователя
     * @return сущность пользователя
     */
    public User mapToUser(RegisterUserDto dto) {
        return userMapper.registerDtoToUser(dto);
    }

    /**
     * Обновляет данные пользователя из DTO.
     *
     * @param dto  DTO с обновленными данными
     * @param user сущность пользователя для обновления
     */
    public void updateUserFromDto(UpdateUserDto dto, User user) {
        userMapper.updateUserFromDto(dto, user);
    }

    /**
     * Получает информацию о текущем аутентифицированном пользователе.
     *
     * @param authentication данные аутентификации
     * @return DTO текущего пользователя
     * @throws NoSuchElementException если пользователь не найден
     */
    public UserDto getCurrentUser(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        return userMapper.userToUserDto(user);
    }

    /**
     * Обновляет пароль текущего пользователя.
     *
     * @param newPasswordDto DTO с текущим и новым паролем
     * @param authentication данные аутентификации
     * @throws IllegalArgumentException если текущий пароль неверен
     */
    @Transactional
    public void updatePassword(NewPasswordDto newPasswordDto, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        if (!passwordEncoder.matches(newPasswordDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Обновляет основные данные пользователя.
     *
     * @param updateUserDto  DTO с обновленными данными
     * @param authentication данные аутентификации
     * @return обновленные данные пользователя
     */
    @Transactional
    public UpdateUserDto updateUser(UpdateUserDto updateUserDto, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        userMapper.updateUserFromDto(updateUserDto, user);
        User savedUser = userRepository.save(user);

        UpdateUserDto responseDto = new UpdateUserDto();
        responseDto.setFirstName(savedUser.getFirstName());
        responseDto.setLastName(savedUser.getLastName());
        responseDto.setPhone(savedUser.getPhone());

        return responseDto;
    }

    /**
     * Обновляет аватар пользователя.
     *
     * @param image          файл изображения
     * @param authentication данные аутентификации
     * @throws IOException если произошла ошибка при работе с изображением
     */
    @Transactional
    public byte[] downloadUserImage(MultipartFile image, Authentication authentication) throws IOException {
        User user = getUserFromAuthentication(authentication);

        if (user.getImagePath() != null) {
            imageService.deleteImage(user.getImagePath());
        }

        String imagePath = imageService.saveUserImage(image);
        user.setImagePath(imagePath);
        userRepository.save(user);
        return image.getBytes();
    }

    /**
     * Получает аватар пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return массив байтов изображения
     * @throws NoSuchElementException если пользователь или его аватар не найдены
     * @throws IOException            если произошла ошибка при чтении изображения
     */
    public byte[] getUserImage(String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (user.getImagePath() == null) {
            throw new NoSuchElementException("User image not found");
        }

        return imageService.getImage(user.getImagePath());
    }

    /**
     * Получает пользователя по данным аутентификации.
     *
     * @param authentication данные аутентификации
     * @return сущность пользователя
     * @throws NoSuchElementException если пользователь не найден
     */
    private User getUserFromAuthentication(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }
}