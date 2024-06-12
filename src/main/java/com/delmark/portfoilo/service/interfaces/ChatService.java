package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.messages.Message;
import com.delmark.portfoilo.models.user.User;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.Set;

public interface ChatService {
    Chat createChat(Set<User> users, String chatName);

    Chat getChatById(Long id);

    Chat addUserToChat(Long userId, Long chatId);

    Optional<Chat> removeUserFromChat(Long userId, Long chatId);

    Set<User> getChatUsers(Long chatId);

    Page<Message> getMessagesByChatId(Long chatId, int page, int size);

}
