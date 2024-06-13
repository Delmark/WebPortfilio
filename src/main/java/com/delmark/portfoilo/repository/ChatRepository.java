package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChatRepository extends JpaRepository<Chat, Long>, PagingAndSortingRepository<Chat, Long> {
    Page<Chat> findByUsersContains(User user, Pageable pageable);
}
