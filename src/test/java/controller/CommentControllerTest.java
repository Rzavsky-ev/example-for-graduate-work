package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.HomeworkApplication;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.service.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = HomeworkApplication.class)
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CreateOrUpdateCommentDto createOrUpdateCommentDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        createOrUpdateCommentDto = new CreateOrUpdateCommentDto();
        createOrUpdateCommentDto.setText("Test comment text");

        commentDto = new CommentDto();
        commentDto.setPk(1);
        commentDto.setText("Test comment text");
        commentDto.setAuthor(1);
        commentDto.setAuthorFirstName("Author");
    }

    @DisplayName("Получение комментариев - должен вернуть список комментариев")
    @Test
    @WithMockUser
    void getCommentsShouldReturnCommentsList() throws Exception {
        when(commentService.getCommentsByAdId(anyInt())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/ads/1/comments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pk").value(1))
                .andExpect(jsonPath("$[0].text").value("Test comment text"));

        verify(commentService, times(1)).getCommentsByAdId(1);
    }

    @DisplayName("Добавление комментария - должен вернуть созданный комментарий")
    @Test
    @WithMockUser
    void addCommentShouldReturnCreatedComment() throws Exception {
        when(commentService.addComment(anyInt(), any(CreateOrUpdateCommentDto.class), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/ads/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrUpdateCommentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.text").value("Test comment text"));

        verify(commentService, times(1))
                .addComment(anyInt(), any(CreateOrUpdateCommentDto.class), any());
    }

    @DisplayName("Удаление комментария - должен вернуть статус No Content")
    @Test
    @WithMockUser
    void deleteCommentShouldReturnNoContent() throws Exception {
        doNothing().when(commentService).deleteComment(anyInt(), anyInt());

        mockMvc.perform(delete("/ads/1/comments/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1, 1);
    }

    @DisplayName("Обновление комментария - должен вернуть обновленный комментарий")
    @Test
    @WithMockUser
    void updateCommentShouldReturnUpdatedComment() throws Exception {
        when(commentService.updateComment(anyInt(), anyInt(), any(CreateOrUpdateCommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(patch("/ads/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrUpdateCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(1))
                .andExpect(jsonPath("$.text").value("Test comment text"));

        verify(commentService, times(1))
                .updateComment(anyInt(), anyInt(), any(CreateOrUpdateCommentDto.class));
    }

    @DisplayName("Получение комментариев без авторизации - должен вернуть статус Unauthorized")
    @Test
    void getCommentsUnauthorizedShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/ads/1/comments"))
                .andExpect(status().isUnauthorized());
    }
}
