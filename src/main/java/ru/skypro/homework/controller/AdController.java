package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/ads")
@Tag(name = "Объявления", description = "Контроллер для работы с объявлениями")
public class AdController {

    @Operation(
            summary = "Получить все объявления",
            description = "Возвращает список всех объявлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список объявлений",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdDto.class)
                            )
                    )
            }
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AdDto> getAllAds() {
        return List.of();
    }

    @Operation(
            summary = "Получить объявление по ID",
            description = "Возвращает полную информацию об объявлении по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденное объявление",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ExtendedAdDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content()
                    )
            }
    )
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ExtendedAdDto getAds(@PathVariable Long id) {
        return new ExtendedAdDto();
    }

    @Operation(
            summary = "Добавить объявление",
            description = "Добавляет новое объявление с изображением",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Созданное объявление",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверные параметры запроса",
                            content = @Content()
                    )
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdDto addAd(@RequestPart("properties") String properties,
                       @RequestPart("image") MultipartFile image) {
        return new AdDto();
    }

    @Operation(
            summary = "Обновить объявление",
            description = "Обновляет информацию об объявлении по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Обновленное объявление",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AdDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content()
                    )
            }
    )
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdDto updateAds(@PathVariable Long id, @RequestBody AdDto adDto) {
        return new AdDto();
    }

    @Operation(
            summary = "Обновить изображение объявления",
            description = "Обновляет изображение для объявления по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изображение успешно обновлено",
                            content = @Content(
                                    mediaType = "application/octet-stream",
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content()
                    )
            }
    )
    @PatchMapping("/{id}/image")
    @ResponseStatus(HttpStatus.OK)
    public byte[] updateImage(@PathVariable Long id,
                              @RequestParam("image") MultipartFile image) {
        return new byte[0];
    }

    @Operation(
            summary = "Удалить объявление",
            description = "Удаляет объявление по его идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Объявление успешно удалено",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Объявление не найдено",
                            content = @Content()
                    )
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAd(@PathVariable Long id) {
    }
}