package ru.skypro.homework.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Comment;

@Tag(
        name = "Comment Repository",
        description = "Интерфейс для выполнения операций CRUD с комментариями"
)
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}