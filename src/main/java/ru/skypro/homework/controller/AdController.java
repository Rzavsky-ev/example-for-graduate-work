package ru.skypro.homework.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import ru.skypro.homework.service.AdService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
@Tag(name = "Объявления", description = "Контроллер для работы с объявлениями")
public class AdController {

    private final AdService adService;

    @Operation(summary = "Получить все объявления",
            responses = @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdDto[].class))))
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdDto> getAllAds() {
        return adService.getAllAds();
    }

    @Operation(summary = "Получить информацию об объявлении",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExtendedAdDto getAdInfo(@PathVariable Long id) {
        return adService.getExtendedAdDto(id);
    }

    @Operation(summary = "Создать новое объявление",
            responses = @ApiResponse(responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdDto.class))))
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AdDto createAd(
            @RequestPart CreateOrUpdateAdDto properties,
            @RequestPart MultipartFile image,
            Authentication authentication
    ) throws IOException {
        return adService.createAd(properties, image, authentication);
    }

    @Operation(summary = "Обновить объявление",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdDto updateAd(
            @PathVariable Integer id,
            @RequestBody CreateOrUpdateAdDto updatedAd
    ) {
        return adService.updateAd(id, updatedAd);
    }

    @Operation(summary = "Удалить объявление",
            responses = {
                    @ApiResponse(responseCode = "204", description = "No Content"),
                    @ApiResponse(responseCode = "404", description = "Not Found")
            })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAd(@PathVariable Integer id) throws IOException {
        adService.deleteAd(id);
    }

    @Operation(summary = "Обновить изображение объявления",
            responses = @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE)))
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public byte[] updateAdImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image
    ) throws IOException {
        return adService.updateAdImage(id, image);
    }

    @Operation(summary = "Получить объявления текущего пользователя",
            responses = @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdDto[].class))))
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public List<AdDto> getMyAds(Authentication authentication) {
        return adService.getAdsByAuthor(authentication);
    }
}