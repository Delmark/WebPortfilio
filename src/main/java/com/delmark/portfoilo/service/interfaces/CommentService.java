package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.messages.Comment;

public interface CommentService {
    Comment createComment(Long portfolioId, String comment);

    Comment getCommentById(Long commentId);

    Comment editCommentById(Long commentId, String comment);

    void deleteCommentById(Long commentId);
}
