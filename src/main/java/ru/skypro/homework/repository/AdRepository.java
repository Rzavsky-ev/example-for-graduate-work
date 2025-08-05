package ru.skypro.homework.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Ad;

@Tag(
        name = "Ad Repository",
        description = "Интерфейс для работы с объявлениями в базе данных"
)
@Repository
public interface AdRepository extends JpaRepository<Ad, Integer> {
}