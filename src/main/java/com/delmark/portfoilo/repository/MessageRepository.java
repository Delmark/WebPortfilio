package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
