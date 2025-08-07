package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(imageService, "uploadDirectory", tempDir.toString());
    }

    @DisplayName("Сохранение изображения объявления - должно сохранять в директорию ads")
    @Test
    void saveAdImageShouldSaveImageToAdsDirectory() throws IOException {

        String content = "test image content";
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                content.getBytes()
        );

        String resultPath = imageService.saveAdImage(imageFile);

        assertNotNull(resultPath);
        assertTrue(resultPath.startsWith("ads/"));
        assertTrue(Files.exists(tempDir.resolve(resultPath)));
        assertEquals(content.length(), Files.size(tempDir.resolve(resultPath)));
    }

    @DisplayName("Сохранение изображения пользователя - должно сохранять в директорию users")
    @Test
    void saveUserImageShouldSaveImageToUsersDirectory() throws IOException {

        String content = "user image content";
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "user.png",
                "image/png",
                content.getBytes()
        );

        String resultPath = imageService.saveUserImage(imageFile);

        assertNotNull(resultPath);
        assertTrue(resultPath.startsWith("users/"));
        assertTrue(Files.exists(tempDir.resolve(resultPath)));
        assertEquals(content.length(), Files.size(tempDir.resolve(resultPath)));
    }

    @DisplayName("Сохранение изображения - должно выбрасывать исключение при пустом файле")
    @Test
    void saveImageShouldThrowExceptionWhenFileIsEmpty() {

        MockMultipartFile emptyFile = new MockMultipartFile(
                "image",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        assertThrows(IllegalArgumentException.class, () -> imageService.saveAdImage(emptyFile));
    }

    @DisplayName("Получение изображения - должно возвращать байты изображения")
    @Test
    void getImageShouldReturnImageBytes() throws IOException {

        String content = "test image content";
        Path testImagePath = tempDir.resolve("ads/test.jpg");
        Files.createDirectories(testImagePath.getParent());
        Files.write(testImagePath, content.getBytes());

        byte[] result = imageService.getImage("ads/test.jpg");

        assertArrayEquals(content.getBytes(), result);
    }

    @DisplayName("Получение изображения - должно выбрасывать исключение если изображение не найдено")
    @Test
    void getImageShouldThrowExceptionWhenImageNotFound() {

        assertThrows(IOException.class, () -> imageService.getImage("nonexistent/image.jpg"));
    }

    @DisplayName("Удаление изображения - должно удалять существующее изображение")
    @Test
    void deleteImageShouldDeleteExistingImage() throws IOException {

        String content = "image to delete";
        Path imageToDeletePath = tempDir.resolve("users/delete_me.jpg");
        Files.createDirectories(imageToDeletePath.getParent());
        Files.write(imageToDeletePath, content.getBytes());

        imageService.deleteImage("users/delete_me.jpg");

        assertFalse(Files.exists(imageToDeletePath));
    }

    @DisplayName("Удаление изображения - не должно выбрасывать исключение если изображение не существует")
    @Test
    void deleteImageShouldNotThrowWhenImageDoesNotExist() {
        assertDoesNotThrow(() -> imageService.deleteImage("nonexistent/image.jpg"));
    }

    @DisplayName("Сохранение изображения - должно создавать директорию если она не существует")
    @Test
    void saveImageShouldCreateDirectoryIfNotExists() {

        String content = "test directory creation";
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "new_dir_test.jpg",
                "image/jpeg",
                content.getBytes()
        );

        Path newDirPath = tempDir.resolve("new_directory");
        assertFalse(Files.exists(newDirPath));

        String resultPath = ReflectionTestUtils.invokeMethod(
                imageService,
                "saveImage",
                imageFile,
                "new_directory"
        );

        assertNotNull(resultPath);
        assertTrue(resultPath.startsWith("new_directory/"));
        assertTrue(Files.exists(tempDir.resolve(resultPath)));
    }

    @DisplayName("Сохранение изображения - должно сохранять расширение файла")
    @Test
    void saveImageShouldPreserveFileExtension() throws IOException {

        String content = "test file extension";
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test_image.jpeg",
                "image/jpeg",
                content.getBytes()
        );

        String resultPath = imageService.saveAdImage(imageFile);

        assertTrue(resultPath.endsWith(".jpeg") || resultPath.endsWith(".jpg"));
    }
}
