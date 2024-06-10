package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.messages.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
