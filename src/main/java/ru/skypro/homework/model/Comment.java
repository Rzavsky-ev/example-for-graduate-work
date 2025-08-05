package ru.skypro.homework.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Schema(description = "Сущность комментария к объявлению")
@Entity
@Table(name = "comments")
@NoArgsConstructor
@Getter
@Setter
public class Comment {

    @Schema(
            description = "Уникальный идентификатор комментария",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Schema(
            description = "Текст комментария",
            example = "Этот товар в отличном состоянии!",
            requiredMode = Schema.RequiredMode.REQUIRED,
            maxLength = 64
    )
    @Column(nullable = false, length = 64)
    private String text;

    @Schema(
            description = "Дата и время создания комментария в миллисекундах с эпохи Unix",
            example = "1678901234567",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Schema(
            description = "Объявление, к которому относится комментарий",
            implementation = Ad.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @Schema(
            description = "Автор комментария",
            implementation = User.class,
            accessMode = Schema.AccessMode.READ_ONLY
    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}
