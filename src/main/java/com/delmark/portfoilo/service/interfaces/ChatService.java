package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.Chat;
import com.delmark.portfoilo.models.Message;
import com.delmark.portfoilo.models.User;

import java.util.List;
import java.util.Set;

public interface ChatService {
    Chat createChat(Set<User> users);

    Chat getChatById(Long id);

    Chat addUserToChat(Long userId, Long chatId);

    Chat removeUserFromChat(Long userId, Long chatId);

    List<User> getChatUsers(Long chatId);

    List<Message> getMessagesByChatId(Long chatId);

    Message addMessageToChat(Long chatId, Message message);

    void deleteMessageFromChat(Long id);

    void deleteChatById(Long id);
}
