package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.exceptions.response.NoSuchMessageException;
import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.messages.Message;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.MessageRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.service.interfaces.ChatService;
import com.delmark.portfoilo.service.interfaces.MessageService;
import com.delmark.portfoilo.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChatService chatService;
    private final RolesRepository rolesRepository;

    @Override
    public Message createMessage(Long chatId, String message) {
        User sender = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());
        Chat chat = chatService.getChatById(chatId);

        if (!chat.getUsers().contains(sender)) {
            throw new AccessDeniedException("Вы не являетесь участником данного чата");
        }

        Message newMessage = new Message().
                setMessage(message).
                setCreatedAt(LocalDateTime.now()).
                setChat(chat).
                setSender(sender);

        return messageRepository.save(newMessage);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageRepository.findById(id).orElseThrow(NoSuchMessageException::new);
    }

    @Override
    public Message editMessageById(Long id, String message) {
        User sender = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());
        Message messageToUpdate = messageRepository.findById(id).orElseThrow(NoSuchMessageException::new);

        if (!messageToUpdate.getSender().getId().equals(sender.getId())) {
            throw new AccessDeniedException("Вы не являетесь автором данного сообщения");
        }

        messageToUpdate.setMessage(message);
        return messageRepository.save(messageToUpdate);
    }

    @Override
    public void deleteMessageById(Long id) {
        User sender = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());
        Message messageToDelete = messageRepository.findById(id).orElseThrow(NoSuchMessageException::new);

        if (!messageToDelete.getSender().getId().equals(sender.getId()) && !sender.getAuthorities().contains(rolesRepository.findByAuthority("ADMIN").get())) {
            throw new AccessDeniedException("Вы не являетесь автором данного сообщения");
        }

        messageRepository.delete(messageToDelete);
    }
}
