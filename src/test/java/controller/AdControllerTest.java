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
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.HomeworkApplication;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.service.AdService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HomeworkApplication.class)
@AutoConfigureMockMvc
public class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdService adService;

    @Autowired
    private WebApplicationContext context;

    private final String USERNAME = "test@example.com";
    private final int AD_ID = 1;
    private final long LONG_AD_ID = 1L;
    private final byte[] IMAGE_BYTES = "test image content".getBytes();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @DisplayName("Получение всех объявлений - должен вернуть список объявлений")
    @Test
    @WithMockUser
    void getAllAdsShouldReturnListOfAds() throws Exception {

        AdDto adDto = new AdDto();
        adDto.setPk(AD_ID);
        adDto.setTitle("Test Ad");
        List<AdDto> adDtos = Collections.singletonList(adDto);

        when(adService.getAllAds()).thenReturn(adDtos);

        mockMvc.perform(get("/ads")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pk").value(AD_ID))
                .andExpect(jsonPath("$[0].title").value("Test Ad"));

        verify(adService, times(1)).getAllAds();
    }

    @DisplayName("Получение информации об объявлении - должен вернуть расширенную информацию")
    @Test
    @WithMockUser
    void getAdInfoShouldReturnExtendedAdInfo() throws Exception {

        ExtendedAdDto extendedAdDto = new ExtendedAdDto();
        extendedAdDto.setPk(AD_ID);
        extendedAdDto.setTitle("Test Ad");

        when(adService.getExtendedAdDto(LONG_AD_ID)).thenReturn(extendedAdDto);

        mockMvc.perform(get("/ads/{id}", AD_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(AD_ID))
                .andExpect(jsonPath("$.title").value("Test Ad"));

        verify(adService, times(1)).getExtendedAdDto(LONG_AD_ID);
    }

    @DisplayName("Создание объявления - должен создать новое объявление без изображения и вернуть статус 201")
    @Test
    @WithMockUser
    void createAdWithoutImageShouldCreateNewAdAndReturnCreatedStatus() throws Exception {
        CreateOrUpdateAdDto createAdDto = new CreateOrUpdateAdDto();
        createAdDto.setTitle("New Ad");
        createAdDto.setDescription("Description");
        createAdDto.setPrice(100);

        AdDto createdAdDto = new AdDto();
        createdAdDto.setPk(AD_ID);
        createdAdDto.setTitle("New Ad");

        when(adService.createAdWithoutImage(any(CreateOrUpdateAdDto.class), any(Authentication.class)))
                .thenReturn(createdAdDto);

        mockMvc.perform(post("/ads/no-image")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAdDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pk").value(AD_ID))
                .andExpect(jsonPath("$.title").value("New Ad"));

        verify(adService, times(1))
                .createAdWithoutImage(any(CreateOrUpdateAdDto.class), any(Authentication.class));
    }

    @DisplayName("Обновление объявления - должен обновить объявление и вернуть статус 200")
    @Test
    @WithMockUser
    void updateAdShouldUpdateAdAndReturnOkStatus() throws Exception {

        CreateOrUpdateAdDto updateAdDto = new CreateOrUpdateAdDto();
        updateAdDto.setTitle("Updated Ad");
        updateAdDto.setDescription("Updated Description");
        updateAdDto.setPrice(200);

        AdDto updatedAdDto = new AdDto();
        updatedAdDto.setPk(AD_ID);
        updatedAdDto.setTitle("Updated Ad");

        when(adService.updateAd(eq(AD_ID), any(CreateOrUpdateAdDto.class))).thenReturn(updatedAdDto);

        mockMvc.perform(patch("/ads/{id}", AD_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAdDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(AD_ID))
                .andExpect(jsonPath("$.title").value("Updated Ad"));

        verify(adService, times(1)).updateAd(eq(AD_ID), any(CreateOrUpdateAdDto.class));
    }

    @DisplayName("Удаление объявления - должен удалить объявление и вернуть статус 204")
    @Test
    @WithMockUser
    void deleteAdShouldDeleteAdAndReturnNoContentStatus() throws Exception {

        doNothing().when(adService).deleteAd(AD_ID);

        mockMvc.perform(delete("/ads/{id}", AD_ID))
                .andExpect(status().isNoContent());

        verify(adService, times(1)).deleteAd(AD_ID);
    }

    @DisplayName("Обновление изображения объявления - должен обновить изображение и вернуть статус 200")
    @Test
    @WithMockUser
    void updateAdImageShouldUpdateImageAndReturnOkStatus() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "new-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                IMAGE_BYTES
        );

        when(adService.updateAdImage(eq(LONG_AD_ID), any(MultipartFile.class))).thenReturn(IMAGE_BYTES);

        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/ads/{id}/image", AD_ID)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().bytes(IMAGE_BYTES));

        verify(adService, times(1)).updateAdImage(eq(LONG_AD_ID), any(MultipartFile.class));
    }

    @DisplayName("Получение объявлений текущего пользователя - должен вернуть список объявлений пользователя")
    @Test
    @WithMockUser(username = USERNAME)
    void getMyAdsShouldReturnUserAds() throws Exception {

        AdDto adDto = new AdDto();
        adDto.setPk(AD_ID);
        adDto.setTitle("My Ad");
        List<AdDto> adDtos = Collections.singletonList(adDto);

        when(adService.getAdsByAuthor(any(Authentication.class))).thenReturn(adDtos);

        mockMvc.perform(get("/ads/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pk").value(AD_ID))
                .andExpect(jsonPath("$[0].title").value("My Ad"));

        verify(adService, times(1)).getAdsByAuthor(any(Authentication.class));
    }
}

