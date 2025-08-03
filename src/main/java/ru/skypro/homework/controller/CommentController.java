package ru.skypro.homework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/ads/{id}/comments")
public class CommentController {

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Integer id) {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping
    public ResponseEntity<CommentDto> addComment(@PathVariable Long id,
                                                 @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(new CommentDto());
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("id") Long adId,
                                                    @PathVariable Long commentId,
                                                    @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(new CommentDto());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long adId,
                                              @PathVariable Long commentId) {
        return ResponseEntity.ok().build();
    }
}