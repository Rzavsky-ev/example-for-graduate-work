package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @InjectMocks
    private AdService adService;

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

    @DisplayName("Получение всех объявлений - должен вернуть список DTO объявлений")
    @Test
    void getAllAdsReturnListOfAdDto() {

        Ad ad1 = new Ad();
        Ad ad2 = new Ad();
        AdDto adDto1 = new AdDto();
        AdDto adDto2 = new AdDto();

        when(adRepository.findAll()).thenReturn(List.of(ad1, ad2));
        when(adMapper.toAdDto(ad1)).thenReturn(adDto1);
        when(adMapper.toAdDto(ad2)).thenReturn(adDto2);

        List<AdDto> result = adService.getAllAds();

        assertEquals(2, result.size());
        assertTrue(result.contains(adDto1));
        assertTrue(result.contains(adDto2));
        verify(adRepository, times(1)).findAll();
    }

    @DisplayName("Получение расширенной информации об объявлении - должен вернуть ExtendedAdDto при существующем объявлении")
    @Test
    void getExtendedAdDtoWhenAdExistsReturnExtendedAdDto() {

        int adId = 1;
        Ad ad = new Ad();
        ExtendedAdDto expectedDto = new ExtendedAdDto();

        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        when(adMapper.toExtendedAdDto(ad)).thenReturn(expectedDto);

        ExtendedAdDto result = adService.getExtendedAdDto((long) adId);

        assertEquals(expectedDto, result);
        verify(adRepository, times(1)).findById(adId);
    }

    @DisplayName("Получение расширенной информации об объявлении - должен выбросить исключение при отсутствии объявления")
    @Test
    void getExtendedAdDtoWhenAdNotExistsThrowException() {

        int adId = 1;
        when(adRepository.findById(adId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> adService.getExtendedAdDto((long) adId));
    }

    @DisplayName("Создание объявления - должен сохранить объявление и вернуть DTO")
    @Test
    void createAdSaveAdAndReturnAdDto() throws IOException {

        CreateOrUpdateAdDto properties = new CreateOrUpdateAdDto();
        properties.setTitle("Test Ad");
        properties.setDescription("Test Description");
        properties.setPrice(1000);

        MultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image".getBytes());
        String imagePath = "/ads/images/1";
        User author = new User();
        author.setUsername("test@example.com");

        Ad newAd = new Ad();
        newAd.setTitle(properties.getTitle());
        newAd.setDescription(properties.getDescription());
        newAd.setPrice(properties.getPrice());
        newAd.setAuthor(author);
        newAd.setImagePath(imagePath);

        Ad savedAd = new Ad();
        savedAd.setId(1);
        AdDto expectedAdDto = new AdDto();

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(author));
        when(imageService.saveAdImage(image)).thenReturn(imagePath);
        when(adRepository.save(any(Ad.class))).thenReturn(savedAd);
        when(adMapper.toAdDto(savedAd)).thenReturn(expectedAdDto);

        AdDto result = adService.createAd(properties, image, authentication);

        assertEquals(expectedAdDto, result);
        verify(userRepository, times(1)).findByUsername("test@example.com");
        verify(imageService, times(1)).saveAdImage(image);
        verify(adRepository, times(1)).save(any(Ad.class));
    }

    @DisplayName("Обновление объявления - должен обновить и вернуть DTO при существующем объявлении")
    @Test
    void updateAdWhenAdExistsUpdateAndReturnAdDto() {

        int adId = 1;
        CreateOrUpdateAdDto updatedAd = new CreateOrUpdateAdDto();
        updatedAd.setTitle("Updated Title");
        updatedAd.setDescription("Updated Description");
        updatedAd.setPrice(2000);

        Ad existingAd = new Ad();
        existingAd.setId(adId);
        existingAd.setTitle("Old Title");
        existingAd.setDescription("Old Description");
        existingAd.setPrice(1000);

        Ad savedAd = new Ad();
        savedAd.setId(adId);
        savedAd.setTitle(updatedAd.getTitle());
        savedAd.setDescription(updatedAd.getDescription());
        savedAd.setPrice(updatedAd.getPrice());

        AdDto expectedAdDto = new AdDto();

        when(adRepository.findById(adId)).thenReturn(Optional.of(existingAd));
        when(adRepository.save(existingAd)).thenReturn(savedAd);
        when(adMapper.toAdDto(savedAd)).thenReturn(expectedAdDto);

        AdDto result = adService.updateAd(adId, updatedAd);

        assertEquals(expectedAdDto, result);
        assertEquals(updatedAd.getTitle(), existingAd.getTitle());
        assertEquals(updatedAd.getDescription(), existingAd.getDescription());
        assertEquals(updatedAd.getPrice(), existingAd.getPrice());
        verify(adRepository, times(1)).findById(adId);
        verify(adRepository, times(1)).save(existingAd);
    }

    @DisplayName("Обновление объявления - должен выбросить исключение при отсутствии объявления")
    @Test
    void updateAdWhenAdNotExistsThrowException() {

        int adId = 1;
        CreateOrUpdateAdDto updatedAd = new CreateOrUpdateAdDto();
        when(adRepository.findById(adId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> adService.updateAd(adId, updatedAd));
    }

    @DisplayName("Удаление объявления - должен удалить объявление и изображение при существующем объявлении")
    @Test
    void deleteAdWhenAdExistsDeleteAdAndImage() throws IOException {

        int adId = 1;
        Ad ad = new Ad();
        ad.setId(adId);
        ad.setImagePath("/ads/images/1");

        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));

        adService.deleteAd(adId);

        verify(imageService, times(1)).deleteImage("/ads/images/1");
        verify(adRepository, times(1)).delete(ad);
    }

    @DisplayName("Обновление изображения объявления - должен обновить изображение и вернуть его байты")
    @Test
    void updateAdImageWhenAdExistsUpdateImageAndReturnBytes() throws IOException {

        int adId = 1;
        MultipartFile newImage = new MockMultipartFile("newImage", "new.jpg",
                "image/jpeg", "new image".getBytes());
        String newImagePath = "/ads/images/2";

        Ad ad = new Ad();
        ad.setId(adId);
        ad.setImagePath("/ads/images/1");

        when(adRepository.findById(adId)).thenReturn(Optional.of(ad));
        when(imageService.saveAdImage(newImage)).thenReturn(newImagePath);

        byte[] result = adService.updateAdImage((long) adId, newImage);

        assertArrayEquals("new image".getBytes(), result);
        assertEquals(newImagePath, ad.getImagePath());
        verify(imageService, times(1)).deleteImage("/ads/images/1");
        verify(imageService, times(1)).saveAdImage(newImage);
        verify(adRepository, times(1)).save(ad);
    }

    @DisplayName("Получение объявлений автора - должен вернуть список DTO объявлений пользователя")
    @Test
    void getAdsByAuthorReturnUserAds() {

        User author = new User();
        author.setUsername("test@example.com");

        Ad ad1 = new Ad();
        Ad ad2 = new Ad();
        AdDto adDto1 = new AdDto();
        AdDto adDto2 = new AdDto();

        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(author));
        when(adRepository.findAllByAuthor(author)).thenReturn(List.of(ad1, ad2));
        when(adMapper.toAdDto(ad1)).thenReturn(adDto1);
        when(adMapper.toAdDto(ad2)).thenReturn(adDto2);

        List<AdDto> result = adService.getAdsByAuthor(authentication);

        assertEquals(2, result.size());
        assertTrue(result.contains(adDto1));
        assertTrue(result.contains(adDto2));
        verify(userRepository, times(1)).findByUsername("test@example.com");
        verify(adRepository, times(1)).findAllByAuthor(author);
    }

}
