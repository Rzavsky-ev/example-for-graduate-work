package ru.skypro.homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skypro.homework.converter.CommentMapper;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


/**
 * Сервис для работы с комментариями к объявлениям.
 * Обеспечивает создание, получение, обновление и удаление комментариев,
 * а также их привязку к объявлениям и пользователям.
 */
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    /**
     * Получает все комментарии для указанного объявления.
     *
     * @param adId ID объявления
     * @return список DTO комментариев
     * @throws NoSuchElementException если объявление не найдено
     */
    public List<CommentDto> getCommentsByAdId(Integer adId) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new NoSuchElementException("Ad not found"));

        return commentRepository.findAllByAd(ad).stream()
                .map(commentMapper::commentToCommentDto)
                .collect(Collectors.toList());
    }

    /**
     * Добавляет новый комментарий к объявлению.
     *
     * @param adId           ID объявления
     * @param commentDto     DTO с данными комментария
     * @param authentication данные аутентификации
     * @return DTO созданного комментария
     * @throws NoSuchElementException если объявление или пользователь не найдены
     */
    @Transactional
    public CommentDto addComment(Integer adId, CreateOrUpdateCommentDto commentDto, Authentication authentication) {
        Ad ad = adRepository.findById(adId)
                .orElseThrow(() -> new NoSuchElementException("Ad not found"));
        User author = getUserFromAuthentication(authentication);

        Comment comment = commentMapper.createCommentDtoToComment(commentDto);
        comment.setAd(ad);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.commentToCommentDto(savedComment);
    }

    /**
     * Обновляет существующий комментарий.
     *
     * @param adId           ID объявления
     * @param commentId      ID комментария
     * @param updatedComment DTO с обновленными данными
     * @return DTO обновленного комментария
     * @throws NoSuchElementException если комментарий не найден или не принадлежит указанному объявлению
     */
    @Transactional
    public CommentDto updateComment(Integer adId, Integer commentId, CreateOrUpdateCommentDto updatedComment) {
        Comment comment = getCommentIfExistsAndBelongsToAd(adId, commentId);

        if (updatedComment.getText() != null && !updatedComment.getText().isBlank()) {
            comment.setText(updatedComment.getText());
        }

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.commentToCommentDto(savedComment);
    }

    /**
     * Удаляет комментарий.
     *
     * @param adId      ID объявления
     * @param commentId ID комментария
     * @throws NoSuchElementException если комментарий не найден или не принадлежит указанному объявлению
     */
    @Transactional
    public void deleteComment(Integer adId, Integer commentId) {
        Comment comment = getCommentIfExistsAndBelongsToAd(adId, commentId);
        commentRepository.delete(comment);
    }

    /**
     * Проверяет существование комментария и его принадлежность к объявлению.
     *
     * @param adId      ID объявления
     * @param commentId ID комментария
     * @return сущность комментария
     * @throws NoSuchElementException если комментарий не найден или не принадлежит указанному объявлению
     */
    private Comment getCommentIfExistsAndBelongsToAd(Integer adId, Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("Comment not found"));

        if (!comment.getAd().getId().equals(adId)) {
            throw new NoSuchElementException("Comment does not belong to the specified ad");
        }

        return comment;
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