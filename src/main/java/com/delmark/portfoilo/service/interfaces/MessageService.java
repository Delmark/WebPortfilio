package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.Message;

public interface MessageService {
    Message createMessage(Long chatId, String message, Long senderId);

    Message getMessageById(Long id);

    Message editMessageById(Long id, String message);

    void deleteMessageById(Long id);
}
