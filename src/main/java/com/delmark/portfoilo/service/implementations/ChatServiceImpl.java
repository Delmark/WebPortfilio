package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.exceptions.response.NoSuchChatException;
import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.messages.Message;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.ChatRepository;
import com.delmark.portfoilo.repository.MessageRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.service.interfaces.ChatService;
import com.delmark.portfoilo.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final MessageRepository messageRepository;
    private final RolesRepository rolesRepository;

    @Override
    public Chat createChat(Set<User> users, String chatName) {
        if (users.size() < 2) {
            throw new IllegalArgumentException("В чате должно не меньше двух участников!");
        }

        if (chatName == null || chatName.isEmpty()) {
            chatName = String.join(" - ", users.stream().map(Object::toString).toList()) + " chat";
        }

        Chat chat = new Chat()
                .setChatName(chatName)
                .setUsers(users);

        return chatRepository.save(chat);
    }

    @Override
    public Chat getChatById(Long id) {
        return chatRepository.findById(id).orElseThrow(NoSuchChatException::new);
    }

    @Override
    public Chat addUserToChat(Long userId, Long chatId) {
        User user = userService.getUserById(userId);
        Chat chat = chatRepository.findById(chatId).orElseThrow(NoSuchChatException::new);

        if (chat.getUsers().contains(user)) {
            throw new IllegalArgumentException("Пользователь уже в чате!");
        }

        chat.getUsers().add(user);
        return chatRepository.save(chat);
    }

    @Override
    public Optional<Chat> removeUserFromChat(Long userId, Long chatId) {
        User user = userService.getUserById(userId);
        Chat chat = chatRepository.findById(chatId).orElseThrow(NoSuchChatException::new);

        if (!chat.getUsers().contains(user)) {
            throw new IllegalArgumentException("Пользователь не в чате!");
        }

        chat.getUsers().remove(user);
        if (chat.getUsers().isEmpty()) {
            chatRepository.delete(chat);
            return Optional.empty();
        }
        else {
            return Optional.of(chatRepository.save(chat));
        }
    }

    @Override
    public Set<User> getChatUsers(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(NoSuchChatException::new).getUsers();
    }

    @Override
    @Transactional
    public Page<Message> getMessagesByChatId(Long chatId, int page, int size) {
        User user = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());
        Chat chat = chatRepository.findById(chatId).orElseThrow(NoSuchChatException::new);

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!getChatUsers(chatId).contains(user) && !user.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("Пользователь не состоит в чате!");
        }

        return messageRepository.findByChat_Id(chatId, PageRequest.of(page, size));
    }
}
