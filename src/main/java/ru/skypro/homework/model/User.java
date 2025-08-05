package ru.skypro.homework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import java.util.List;

@Schema(description = "Сущность пользователя системы")
@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Schema(
            description = "Уникальный идентификатор пользователя",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Schema(
            description = "Логин пользователя (email)",
            example = "user@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 32
    )
    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Schema(
            description = "Пароль пользователя",
            example = "$2a$10$N9qo8uLOickgx2ZMRZoMy...",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 128,
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    @Column(nullable = false, length = 128)
    private String password;

    @Schema(
            description = "Имя пользователя",
            example = "Иван",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 16
    )
    @Column(name = "first_name", nullable = false, length = 16)
    private String firstName;

    @Schema(
            description = "Фамилия пользователя",
            example = "Иванов",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 16
    )
    @Column(name = "last_name", nullable = false, length = 16)
    private String lastName;

    @Schema(
            description = "Телефон пользователя в формате +7 XXX XXX-XX-XX",
            example = "+7 123 456-78-90",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Column(nullable = false)
    private String phone;

    @Schema(
            description = "Роль пользователя в системе",
            example = "USER",
            requiredMode = Schema.RequiredMode.REQUIRED,
            implementation = Role.class
    )
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Schema(
            description = "Путь к аватару пользователя",
            example = "/users/images/1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Column(name = "image_path")
    private String imagePath;

    @Schema(
            description = "Список объявлений пользователя",
            implementation = Ad.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Ad> ads;

    @Schema(
            description = "Список комментариев пользователя",
            implementation = Comment.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Comment> comments;
}