package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.messages.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MessageRepository extends JpaRepository<Message, Long>, PagingAndSortingRepository<Message, Long> {
    Page<Message> findByChat_Id(Long chatId, Pageable pageable);
}
