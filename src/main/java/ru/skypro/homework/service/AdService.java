package ru.skypro.homework.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skypro.homework.converter.AdMapper;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;

@Tag(
        name = "Ad Service",
        description = "Сервис для обработки бизнес-логики связанной с объявлениями"
)
@Service
@RequiredArgsConstructor
public class AdService {

    private final AdMapper adMapper;

    @Operation(
            summary = "Преобразовать Ad в AdDto",
            description = "Конвертирует сущность объявления в DTO для краткого отображения"
    )
    public AdDto mapToAdDto(Ad ad) {
        return adMapper.toAdDto(ad);
    }

    @Operation(
            summary = "Преобразовать Ad в ExtendedAdDto",
            description = "Конвертирует сущность объявления в DTO для подробного отображения"
    )
    public ExtendedAdDto mapToExtendedAdDto(Ad ad) {
        return adMapper.toExtendedAdDto(ad);
    }

    @Operation(
            summary = "Создать Ad из DTO",
            description = "Создает новую сущность объявления на основе DTO и автора"
    )
    public Ad mapToAd(CreateOrUpdateAdDto dto, User author) {
        Ad ad = new Ad();
        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setPrice(dto.getPrice());
        ad.setAuthor(author);
        return ad;
    }

    @Operation(
            summary = "Обновить Ad из DTO",
            description = "Обновляет существующую сущность объявления на основе данных из DTO"
    )
    public void updateAdFromDto(CreateOrUpdateAdDto dto, Ad ad) {
        if (dto.getTitle() != null) {
            ad.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            ad.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            ad.setPrice(dto.getPrice());
        }
    }
}