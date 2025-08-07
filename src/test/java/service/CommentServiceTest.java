package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import ru.skypro.homework.converter.CommentMapper;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.CommentService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private Authentication authentication;

    private Ad testAd;
    private User testUser;
    private Comment testComment;
    private CommentDto testCommentDto;
    private CreateOrUpdateCommentDto testCreateCommentDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("user@example.com");
        testUser.setFirstName("John");
        testUser.setImagePath("/images/user1.jpg");

        testAd = new Ad();
        testAd.setId(1);
        testAd.setAuthor(testUser);

        testComment = new Comment();
        testComment.setId(1);
        testComment.setText("Test comment");
        testComment.setAuthor(testUser);
        testComment.setAd(testAd);
        testComment.setCreatedAt(System.currentTimeMillis());

        testCommentDto = new CommentDto();
        testCommentDto.setPk(1);
        testCommentDto.setText("Test comment");
        testCommentDto.setAuthor(1);
        testCommentDto.setAuthorFirstName("John");
        testCommentDto.setAuthorImage("/images/user1.jpg");

        testCreateCommentDto = new CreateOrUpdateCommentDto();
        testCreateCommentDto.setText("New comment");
    }

    @DisplayName("Получение комментариев по ID объявления - успешный сценарий")
    @Test
    void getCommentsByAdIdShouldReturnCommentsList() {

        when(adRepository.findById(1)).thenReturn(Optional.of(testAd));
        when(commentRepository.findAllByAd(testAd)).thenReturn(List.of(testComment));
        when(commentMapper.commentToCommentDto(testComment)).thenReturn(testCommentDto);

        List<CommentDto> result = commentService.getCommentsByAdId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCommentDto, result.get(0));

        verify(adRepository).findById(1);
        verify(commentRepository).findAllByAd(testAd);
        verify(commentMapper).commentToCommentDto(testComment);
    }

    @DisplayName("Получение комментариев - объявление не найдено")
    @Test
    void getCommentsByAdIdAdNotFoundShouldThrowException() {

        when(adRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> commentService.getCommentsByAdId(1));
        verify(adRepository).findById(1);
        verifyNoInteractions(commentRepository, commentMapper);
    }

    @DisplayName("Добавление нового комментария - успешный сценарий")
    @Test
    void addCommentShouldSaveAndReturnComment() {

        when(adRepository.findById(1)).thenReturn(Optional.of(testAd));
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByUsername("user@example.com")).thenReturn(Optional.of(testUser));
        when(commentMapper.createCommentDtoToComment(testCreateCommentDto)).thenReturn(testComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(commentMapper.commentToCommentDto(testComment)).thenReturn(testCommentDto);

        CommentDto result = commentService.addComment(1, testCreateCommentDto, authentication);

        assertNotNull(result);
        assertEquals(testCommentDto, result);

        verify(adRepository).findById(1);
        verify(authentication).getName();
        verify(userRepository).findByUsername("user@example.com");
        verify(commentMapper).createCommentDtoToComment(testCreateCommentDto);
        verify(commentRepository).save(testComment);
        verify(commentMapper).commentToCommentDto(testComment);
    }

    @DisplayName("Добавление комментария - объявление не найдено")
    @Test
    void addCommentAdNotFoundShouldThrowException() {

        when(adRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> commentService.addComment(1, testCreateCommentDto, authentication));

        verify(adRepository).findById(1);
        verifyNoMoreInteractions(adRepository, userRepository, commentRepository, commentMapper);
    }

    @DisplayName("Обновление комментария - успешный сценарий")
    @Test
    void updateCommentShouldUpdateAndReturnComment() {

        CreateOrUpdateCommentDto updateDto = new CreateOrUpdateCommentDto();
        updateDto.setText("Updated comment");

        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(testComment)).thenReturn(testComment);

        CommentDto updatedDto = new CommentDto();
        updatedDto.setPk(1);
        updatedDto.setText("Updated comment");
        when(commentMapper.commentToCommentDto(testComment)).thenReturn(updatedDto);

        CommentDto result = commentService.updateComment(1, 1, updateDto);

        assertNotNull(result);
        assertEquals("Updated comment", result.getText());

        verify(commentRepository).findById(1);
        verify(commentRepository).save(testComment);
        verify(commentMapper).commentToCommentDto(testComment);
    }

    @DisplayName("Обновление комментария - комментарий не принадлежит объявлению")
    @Test
    void updateCommentCommentDoesNotBelongToAdShouldThrowException() {

        Ad anotherAd = new Ad();
        anotherAd.setId(2);
        testComment.setAd(anotherAd);

        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        assertThrows(NoSuchElementException.class,
                () -> commentService.updateComment(1, 1, testCreateCommentDto));

        verify(commentRepository).findById(1);
        verifyNoMoreInteractions(commentRepository, commentMapper);
    }

    @DisplayName("Удаление комментария - успешный сценарий")
    @Test
    void deleteCommentShouldDeleteComment() {

        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        doNothing().when(commentRepository).delete(testComment);

        commentService.deleteComment(1, 1);

        verify(commentRepository).findById(1);
        verify(commentRepository).delete(testComment);
    }

    @DisplayName("Удаление комментария - комментарий не найден")
    @Test
    void deleteCommentCommentNotFoundShouldThrowException() {

        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> commentService.deleteComment(1, 1));
        verify(commentRepository).findById(1);
        verifyNoMoreInteractions(commentRepository);
    }

    @DisplayName("Преобразование Comment в CommentDto")
    @Test
    void mapToCommentDtoShouldReturnCommentDto() {

        when(commentMapper.commentToCommentDto(testComment)).thenReturn(testCommentDto);

        CommentDto result = commentService.mapToCommentDto(testComment);

        assertEquals(testCommentDto, result);
        verify(commentMapper).commentToCommentDto(testComment);
    }

    @DisplayName("Преобразование CreateOrUpdateCommentDto в Comment")
    @Test
    void mapToCommentShouldReturnComment() {

        when(commentMapper.createCommentDtoToComment(testCreateCommentDto)).thenReturn(testComment);

        Comment result = commentService.mapToComment(testCreateCommentDto, testUser, testAd);

        assertNotNull(result);
        assertEquals(testComment, result);
        assertEquals(testUser, result.getAuthor());
        assertEquals(testAd, result.getAd());

        verify(commentMapper).createCommentDtoToComment(testCreateCommentDto);
    }

    @DisplayName("Обновление текста комментария из DTO")
    @Test
    void updateCommentFromDtoShouldUpdateCommentText() {

        CreateOrUpdateCommentDto updateDto = new CreateOrUpdateCommentDto();
        updateDto.setText("Updated text");

        Comment comment = new Comment();
        comment.setText("Original text");

        commentService.updateCommentFromDto(updateDto, comment);

        assertEquals("Updated text", comment.getText());
    }
}