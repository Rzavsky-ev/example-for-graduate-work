package ru.skypro.homework.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Сервис для работы с изображениями.
 * Обеспечивает сохранение, получение и удаление изображений на файловой системе.
 * Поддерживает раздельное хранение изображений для объявлений и пользователей.
 */
@Service
public class ImageService {

    @Value("${image.upload.directory}")
    private String uploadDirectory;

    /**
     * Сохраняет изображение для объявления.
     *
     * @param image файл изображения
     * @return относительный путь к сохраненному изображению
     * @throws IOException              если произошла ошибка ввода-вывода
     * @throws IllegalArgumentException если передан пустой файл
     */
    public String saveAdImage(MultipartFile image) throws IOException {
        return saveImage(image, "ads");
    }

    /**
     * Сохраняет изображение для пользователя.
     *
     * @param image файл изображения
     * @return относительный путь к сохраненному изображению
     * @throws IOException              если произошла ошибка ввода-вывода
     * @throws IllegalArgumentException если передан пустой файл
     */
    public String saveUserImage(MultipartFile image) throws IOException {
        return saveImage(image, "users");
    }

    /**
     * Сохраняет изображение в указанную поддиректорию.
     *
     * @param image        файл изображения
     * @param subdirectory поддиректория для сохранения ("ads" или "users")
     * @return относительный путь к сохраненному изображению
     * @throws IOException              если произошла ошибка ввода-вывода
     * @throws IllegalArgumentException если передан пустой файл
     */
    private String saveImage(MultipartFile image, String subdirectory) throws IOException {
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        String originalFilename = image.getOriginalFilename();
        assert originalFilename != null;
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + fileExtension;

        Path uploadPath = Paths.get(uploadDirectory, subdirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(image.getInputStream(), filePath);

        return Paths.get(subdirectory, uniqueFilename).toString();
    }

    /**
     * Получает изображение в виде массива байт.
     *
     * @param imagePath относительный путь к изображению
     * @return массив байт изображения
     * @throws IOException если изображение не найдено или произошла ошибка чтения
     */
    public byte[] getImage(String imagePath) throws IOException {
        Path fullPath = Paths.get(uploadDirectory, imagePath);
        if (!Files.exists(fullPath)) {
            throw new IOException("Image not found");
        }
        return Files.readAllBytes(fullPath);
    }

    /**
     * Удаляет изображение по указанному пути.
     *
     * @param imagePath относительный путь к изображению
     * @throws IOException если произошла ошибка при удалении файла
     */
    public void deleteImage(String imagePath) throws IOException {
        Path fullPath = Paths.get(uploadDirectory, imagePath);
        if (Files.exists(fullPath)) {
            Files.delete(fullPath);
        }
    }
}
