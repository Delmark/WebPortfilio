package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.messages.Comment;

import java.util.List;

public interface CommentService {
    Comment createComment(Long portfolioId, String comment);

    List<Comment> getCommentsByPortfolio(Long portfolioId);

    Comment getCommentById(Long commentId);

    Comment editCommentById(Long commentId, String comment);

    void deleteCommentById(Long commentId);
}
