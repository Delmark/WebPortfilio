package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.messages.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
