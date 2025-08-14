package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.converter.AdMapper;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.ImageService;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @Mock
    private AdRepository adRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdMapper adMapper;
    @Mock
    private ImageService imageService;
    @Mock
    private Authentication authentication;
    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private AdService adService;

    private final User testUser = new User();
    private final Ad testAd = new Ad();
    private final AdDto testAdDto = new AdDto();
    private final ExtendedAdDto testExtendedAdDto = new ExtendedAdDto();
    private final CreateOrUpdateAdDto testCreateOrUpdateAdDto = new CreateOrUpdateAdDto();

    @DisplayName("Получение всех объявлений - должен вернуть список AdDto, когда объявления существуют")
    @Test
    void getAllAdsShouldReturnListOfAdDtoWhenAdsExist() {

        when(adRepository.findAll()).thenReturn(List.of(testAd));
        when(adMapper.toAdDto(testAd)).thenReturn(testAdDto);

        List<AdDto> result = adService.getAllAds();

        assertEquals(1, result.size());
        assertEquals(testAdDto, result.get(0));
        verify(adRepository).findAll();
        verify(adMapper).toAdDto(testAd);
    }

    @DisplayName("Получение расширенного AdDto - должен вернуть ExtendedAdDto, когда объявление существует")
    @Test
    void getExtendedAdDtoShouldReturnExtendedAdDtoWhenAdExists() {

        Long adId = 1L;
        when(adRepository.findById(adId.intValue())).thenReturn(Optional.of(testAd));
        when(adMapper.toExtendedAdDto(testAd)).thenReturn(testExtendedAdDto);

        ExtendedAdDto result = adService.getExtendedAdDto(adId);

        assertEquals(testExtendedAdDto, result);
        verify(adRepository).findById(adId.intValue());
        verify(adMapper).toExtendedAdDto(testAd);
    }

    @DisplayName("Получение расширенного AdDto - должен выбросить исключение, когда объявление не найдено")
    @Test
    void getExtendedAdDtoShouldThrowNoSuchElementExceptionWhenAdNotFound() {

        Long adId = 1L;
        when(adRepository.findById(adId.intValue())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> adService.getExtendedAdDto(adId));
        verify(adRepository).findById(adId.intValue());
    }

    @DisplayName("Создание объявления без изображения - должен вернуть AdDto при валидных данных")
    @Test
    void createAdWithoutImageShouldReturnAdDtoWhenValidInput() {

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(testUser));
        when(adRepository.save(any(Ad.class))).thenReturn(testAd);
        when(adMapper.toAdDto(testAd)).thenReturn(testAdDto);

        AdDto result = adService.createAdWithoutImage(testCreateOrUpdateAdDto, authentication);

        assertEquals(testAdDto, result);
        verify(userRepository).findByUsername("username");
        verify(adRepository).save(any(Ad.class));
        verify(adMapper).toAdDto(testAd);
    }

    @DisplayName("Загрузка изображения объявления - должен сохранить изображение и вернуть байты при валидных данных")
    @Test
    void uploadAdImageShouldSaveImageAndReturnBytesWhenValidInput() throws IOException {

        Integer adId = 1;
        when(adRepository.findById(adId)).thenReturn(Optional.of(testAd));
        when(imageFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(imageService.saveAdImage(imageFile)).thenReturn("imagePath");

        byte[] result = adService.uploadAdImage(adId, imageFile);

        assertArrayEquals(new byte[]{1, 2, 3}, result);
        assertEquals("imagePath", testAd.getImagePath());
        verify(adRepository).findById(adId);
        verify(imageService).saveAdImage(imageFile);
        verify(adRepository).save(testAd);
    }

    @DisplayName("Создание объявления с изображением - должен создать объявление с изображением при валидных данных")
    @Test
    void createAdShouldCreateAdWithImageWhenValidInput() throws IOException {

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(testUser));

        Ad savedAdWithoutImage = new Ad();
        savedAdWithoutImage.setId(1);
        when(adRepository.save(any(Ad.class))).thenReturn(savedAdWithoutImage);

        AdDto adDtoWithId = new AdDto();
        adDtoWithId.setPk(1);
        when(adMapper.toAdDto(savedAdWithoutImage)).thenReturn(adDtoWithId);

        when(imageFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(imageService.saveAdImage(imageFile)).thenReturn("imagePath");

        when(adRepository.findById(1)).thenReturn(Optional.of(savedAdWithoutImage));

        AdDto result = adService.createAd(testCreateOrUpdateAdDto, imageFile, authentication);

        assertEquals(adDtoWithId, result);
        verify(userRepository).findByUsername("username");
        verify(adRepository, times(2)).save(any(Ad.class));
        verify(adMapper).toAdDto(savedAdWithoutImage);
        verify(imageService).saveAdImage(imageFile);
        verify(adRepository).findById(1);
    }

    @DisplayName("Обновление объявления - должен обновить объявление при валидных данных")
    @Test
    void updateAdShouldUpdateAdWhenValidInput() {

        Integer adId = 1;
        when(adRepository.findById(adId)).thenReturn(Optional.of(testAd));
        when(adRepository.save(testAd)).thenReturn(testAd);
        when(adMapper.toAdDto(testAd)).thenReturn(testAdDto);
        testCreateOrUpdateAdDto.setTitle("New Title");
        testCreateOrUpdateAdDto.setDescription("New Description");
        testCreateOrUpdateAdDto.setPrice(100);

        AdDto result = adService.updateAd(adId, testCreateOrUpdateAdDto);

        assertEquals(testAdDto, result);
        assertEquals("New Title", testAd.getTitle());
        assertEquals("New Description", testAd.getDescription());
        assertEquals(100, testAd.getPrice());
        verify(adRepository).findById(adId);
        verify(adRepository).save(testAd);
        verify(adMapper).toAdDto(testAd);
    }

    @DisplayName("Удаление объявления - должен удалить объявление и его изображение, когда объявление существует")
    @Test
    void deleteAdShouldDeleteAdAndImageWhenAdExists() throws IOException {

        Integer adId = 1;
        testAd.setImagePath("imagePath");
        when(adRepository.findById(adId)).thenReturn(Optional.of(testAd));

        adService.deleteAd(adId);

        verify(adRepository).findById(adId);
        verify(imageService).deleteImage("imagePath");
        verify(adRepository).delete(testAd);
    }

    @DisplayName("Обновление изображения объявления - должен обновить изображение и вернуть байты при валидных данных")
    @Test
    void updateAdImageShouldUpdateImageAndReturnBytesWhenValidInput() throws IOException {
        Long adId = 1L;
        testAd.setImagePath("oldImagePath");
        when(adRepository.findById(adId.intValue())).thenReturn(Optional.of(testAd));
        when(imageFile.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(imageService.saveAdImage(imageFile)).thenReturn("newImagePath");

        byte[] result = adService.updateAdImage(adId, imageFile);

        assertArrayEquals(new byte[]{1, 2, 3}, result);
        assertEquals("newImagePath", testAd.getImagePath());
        verify(adRepository).findById(adId.intValue());
        verify(imageService).deleteImage("oldImagePath");
        verify(imageService).saveAdImage(imageFile);
        verify(adRepository).save(testAd);
    }

    @DisplayName("Получение объявлений автора - должен вернуть список AdDto, когда у пользователя есть объявления")
    @Test
    void getAdsByAuthorShouldReturnListOfAdDtoWhenUserHasAds() {

        when(authentication.getName()).thenReturn("username");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(testUser));
        when(adRepository.findAllByAuthor(testUser)).thenReturn(List.of(testAd));
        when(adMapper.toAdDto(testAd)).thenReturn(testAdDto);

        List<AdDto> result = adService.getAdsByAuthor(authentication);

        assertEquals(1, result.size());
        assertEquals(testAdDto, result.get(0));
        verify(userRepository).findByUsername("username");
        verify(adRepository).findAllByAuthor(testUser);
        verify(adMapper).toAdDto(testAd);
    }
}