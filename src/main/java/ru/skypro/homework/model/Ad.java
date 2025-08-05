package ru.skypro.homework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Schema(description = "Сущность объявления (рекламного объявления)")
@Entity
@Table(name = "ads")
@NoArgsConstructor
@Getter
@Setter
public class Ad {

    @Schema(
            description = "Уникальный идентификатор объявления",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Schema(
            description = "Заголовок объявления",
            example = "Продам велосипед",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 32
    )
    @Column(nullable = false, length = 32)
    private String title;

    @Schema(
            description = "Описание товара/услуги",
            example = "Отличный горный велосипед, 2022 года выпуска",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 64
    )
    @Column(nullable = false, length = 64)
    private String description;

    @Schema(
            description = "Цена товара/услуги в копейках/центах",
            example = "1500000",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0"
    )
    @Column(nullable = false)
    private Integer price;

    @Schema(
            description = "Путь к изображению товара",
            example = "/ads/images/1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Column(name = "image_path")
    private String imagePath;

    @Schema(
            description = "Автор объявления (пользователь)",
            implementation = User.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Schema(
            description = "Список комментариев к объявлению",
            implementation = Comment.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<Comment> comments;
}
