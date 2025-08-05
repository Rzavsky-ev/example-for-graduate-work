package ru.skypro.homework.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.User;

@Tag(
        name = "User Repository",
        description = "Интерфейс для выполнения операций с пользователями"
)
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}