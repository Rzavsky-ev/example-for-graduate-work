package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.converter.AdMapper;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Сервис для работы с объявлениями (Ad).
 * Предоставляет методы для создания, получения, обновления и удаления объявлений,
 * а также работы с изображениями объявлений.
 */
@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final ImageService imageService;

    /**
     * Преобразует сущность Ad в DTO объявления.
     *
     * @param ad сущность объявления
     * @return DTO объявления
     */
    public AdDto mapToAdDto(Ad ad) {
        return adMapper.toAdDto(ad);
    }

    /**
     * Преобразует сущность Ad в расширенное DTO объявления.
     *
     * @param ad сущность объявления
     * @return расширенное DTO объявления
     */
    public ExtendedAdDto mapToExtendedAdDto(Ad ad) {
        return adMapper.toExtendedAdDto(ad);
    }

    /**
     * Преобразует DTO создания/обновления в сущность Ad.
     *
     * @param dto    DTO создания/обновления объявления
     * @param author автор объявления
     * @return сущность объявления
     */
    public Ad mapToAd(CreateOrUpdateAdDto dto, User author) {
        Ad ad = new Ad();
        ad.setTitle(dto.getTitle());
        ad.setDescription(dto.getDescription());
        ad.setPrice(dto.getPrice());
        ad.setAuthor(author);
        return ad;
    }

    /**
     * Получает список всех объявлений.
     *
     * @return список DTO объявлений
     */
    public List<AdDto> getAllAds() {
        return adRepository.findAll().stream()
                .map(adMapper::toAdDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает расширенное DTO объявления по ID.
     *
     * @param id ID объявления
     * @return расширенное DTO объявления
     * @throws NoSuchElementException если объявление не найдено
     */
    public ExtendedAdDto getExtendedAdDto(Long id) {
        Ad ad = adRepository.findById(id.intValue())
                .orElseThrow(() -> new NoSuchElementException("Ad not found"));
        return adMapper.toExtendedAdDto(ad);
    }

    /**
     * Создает новое объявление.
     *
     * @param properties     DTO с данными для создания объявления
     * @param image          изображение объявления
     * @param authentication данные аутентификации
     * @return DTO созданного объявления
     * @throws IOException            при ошибках работы с изображением
     * @throws NoSuchElementException если пользователь не найден
     */
    @Transactional
    public AdDto createAd(CreateOrUpdateAdDto properties, MultipartFile image, Authentication authentication) throws IOException {
        User author = getUserFromAuthentication(authentication);
        Ad ad = new Ad();
        ad.setTitle(properties.getTitle());
        ad.setDescription(properties.getDescription());
        ad.setPrice(properties.getPrice());
        ad.setAuthor(author);

        String imagePath = imageService.saveAdImage(image);
        ad.setImagePath(imagePath);

        Ad savedAd = adRepository.save(ad);
        return adMapper.toAdDto(savedAd);
    }

    /**
     * Обновляет существующее объявление.
     *
     * @param id        ID объявления
     * @param updatedAd DTO с обновленными данными
     * @return DTO обновленного объявления
     * @throws NoSuchElementException если объявление не найдено
     */
    @Transactional
    public AdDto updateAd(Long id, CreateOrUpdateAdDto updatedAd) {
        Ad ad = adRepository.findById(id.intValue())
                .orElseThrow(() -> new NoSuchElementException("Ad not found"));

        if (updatedAd.getTitle() != null) {
            ad.setTitle(updatedAd.getTitle());
        }
        if (updatedAd.getDescription() != null) {
            ad.setDescription(updatedAd.getDescription());
        }
        if (updatedAd.getPrice() != null) {
            ad.setPrice(updatedAd.getPrice());
        }

        Ad savedAd = adRepository.save(ad);
        return adMapper.toAdDto(savedAd);
    }

    /**
     * Удаляет объявление.
     *
     * @param id ID объявления
     * @throws IOException            при ошибках удаления изображения
     * @throws NoSuchElementException если объявление не найдено
     */
    @Transactional
    public void deleteAd(Long id) throws IOException {
        Ad ad = adRepository.findById(id.intValue())
                .orElseThrow(() -> new NoSuchElementException("Ad not found"));

        imageService.deleteImage(ad.getImagePath());
        adRepository.delete(ad);
    }

    /**
     * Обновляет изображение объявления.
     *
     * @param id    ID объявления
     * @param image новое изображение
     * @return байты нового изображения
     * @throws IOException            при ошибках работы с изображением
     * @throws NoSuchElementException если объявление не найдено
     */
    @Transactional
    public byte[] updateAdImage(Long id, MultipartFile image) throws IOException {
        Ad ad = adRepository.findById(id.intValue())
                .orElseThrow(() -> new NoSuchElementException("Ad not found"));

        imageService.deleteImage(ad.getImagePath());
        String newImagePath = imageService.saveAdImage(image);
        ad.setImagePath(newImagePath);
        adRepository.save(ad);

        return image.getBytes();
    }

    /**
     * Получает список объявлений текущего пользователя.
     *
     * @param authentication данные аутентификации
     * @return список DTO объявлений пользователя
     * @throws NoSuchElementException если пользователь не найден
     */
    public List<AdDto> getAdsByAuthor(Authentication authentication) {
        User author = getUserFromAuthentication(authentication);
        return adRepository.findAllByAuthor(author).stream()
                .map(adMapper::toAdDto)
                .collect(Collectors.toList());
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