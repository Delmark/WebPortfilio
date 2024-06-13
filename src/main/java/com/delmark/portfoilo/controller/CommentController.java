package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.messages.Comment;
import com.delmark.portfoilo.service.interfaces.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Portfolio Comments", description = "Portfolio Comments API")
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(
            @RequestParam("portfolioId") Long portfolioId,
            @RequestParam("comment") String comment
    ) {
        return ResponseEntity.ok(commentService.createComment(portfolioId, comment));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Comment> editComment(
            @PathVariable("id") Long id,
            @RequestParam("comment") String comment
    ){
        return ResponseEntity.ok(commentService.editCommentById(id, comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long id) {
        commentService.deleteCommentById(id);
        return ResponseEntity.ok().build();
    }
}
