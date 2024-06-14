package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.exceptions.response.NoSuchCommentException;
import com.delmark.portfoilo.models.messages.Comment;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.CommentRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.service.interfaces.CommentService;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import com.delmark.portfoilo.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PortfolioService portfolioService;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final RolesRepository rolesRepository;

    @Override
    public Comment createComment(Long portfolioId, String comment) {
        Portfolio portfolio = portfolioService.getPortfolio(portfolioId);
        User sender = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        Comment newComment = new Comment().
                setComment(comment).
                setCreatedAt(LocalDateTime.now()).
                setSender(sender).
                setPortfolio(portfolio);

        return commentRepository.save(newComment);
    }

    @Override
    public List<Comment> getCommentsByPortfolio(Long portfolioId) {
        return commentRepository.findByPortfolioOrderByCreatedAtDesc(portfolioService.getPortfolio(portfolioId));
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(NoSuchCommentException::new);
    }

    @Override
    public Comment editCommentById(Long commentId, String comment) {
        Comment commentForEdit = commentRepository.findById(commentId).orElseThrow(NoSuchCommentException::new);
        User sender = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        if (!commentForEdit.getSender().getId().equals(sender.getId())) {
            throw new AccessDeniedException("Вы не являетесь автором данного комментария");
        }

        commentForEdit.setComment(comment);
        return commentRepository.save(commentForEdit);
    }

    @Override
    public void deleteCommentById(Long commentId) {
        Comment commentForDelete = commentRepository.findById(commentId).orElseThrow(NoSuchCommentException::new);
        User sender = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        if (!commentForDelete.getSender().getId().equals(sender.getId()) && !sender.getAuthorities().contains(rolesRepository.findByAuthority("ADMIN").get())) {
            throw new AccessDeniedException("Вы не являетесь автором данного комментария");
        }

        commentRepository.deleteById(commentId);
    }
}
