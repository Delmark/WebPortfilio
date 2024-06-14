package com.delmark.portfoilo.service;

import com.delmark.portfoilo.exceptions.response.ChatPrincipalsCountException;
import com.delmark.portfoilo.exceptions.response.NoSuchChatException;
import com.delmark.portfoilo.exceptions.response.UserNotFoundException;
import com.delmark.portfoilo.models.DTO.ChatCreationDTO;
import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.*;
import com.delmark.portfoilo.service.implementations.ChatServiceImpl;
import com.delmark.portfoilo.service.implementations.UserServiceImpl;
import com.delmark.portfoilo.service.interfaces.ChatService;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.delmark.portfoilo.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class ChatServiceTest {
    private static final PortfolioRepository portfolioRepository = Mockito.mock(PortfolioRepository.class);
    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static final RolesRepository rolesRepository = Mockito.mock(RolesRepository.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final UserService userService = new UserServiceImpl(userRepository, rolesRepository, passwordEncoder, portfolioRepository);
    private static final ChatRepository chatRepository = Mockito.mock(ChatRepository.class);
    private static final MessageRepository messageRepository = Mockito.mock(MessageRepository.class);
    private static final ChatService chatService = new ChatServiceImpl(chatRepository, userService, messageRepository, rolesRepository);

    @Test
    @WithMockCustomUser
    void createChat() {
        User chatOwner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User chatMember = new User().setId(2L).setName("Biba");

        ChatCreationDTO dto = new ChatCreationDTO("chat", new HashSet<>(List.of(1L, 2L)));
        Chat chatForSave = new Chat(null, "chat", Set.of(chatOwner, chatMember), new ArrayList<>());
        Chat expectedChat = new Chat(1L, "chat", Set.of(chatOwner, chatMember), new ArrayList<>());

        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(chatOwner));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(chatOwner));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(chatMember));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(chatRepository.save(chatForSave)).thenReturn(expectedChat);

        assertEquals(expectedChat, chatService.createChat(dto));
    }

    @Test
    @WithMockCustomUser
    void createChatWithoutOwner() {
        User chatOwner = new User().setId(3L).setName("Boba");
        User chatMember = new User().setId(2L).setName("Biba");

        ChatCreationDTO dto = new ChatCreationDTO("chat", new HashSet<>(List.of(2L, 3L)));

        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(chatOwner));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.of(chatOwner));
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.of(chatMember));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(AccessDeniedException.class, () -> chatService.createChat(dto));
    }

    @Test
    @WithMockCustomUser
    void createLonelyChat() {
        User chatOwner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ChatCreationDTO dto = new ChatCreationDTO("chat", new HashSet<>(List.of(1L)));

        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(chatOwner));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(chatOwner));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(ChatPrincipalsCountException.class, () -> chatService.createChat(dto));
    }

    @Test
    @WithMockCustomUser
    void getChatById() {
        User client = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chat existingChat = new Chat(1L, "chat", Set.of(client, new User().setId(2L).setName("Biba")), null);

        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.of(existingChat));
        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(client));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertEquals(existingChat, chatService.getChatById(1L));
    }

    @Test
    @WithMockCustomUser
    void getChatByNonExistingId() {
        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchChatException.class, () -> chatService.getChatById(1L));
    }

    @Test
    @WithMockCustomUser
    void getChatByStranger() {
        User client = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chat existingChat = new Chat(1L, "chat", Set.of(new User().setId(3L).setName("Boba"), new User().setId(2L).setName("Biba")), null);

        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.of(existingChat));
        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(client));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(AccessDeniedException.class, () -> chatService.getChatById(1L));
    }

    @Test
    @WithMockCustomUser
    void addUserToChat() {
        User client = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User existingMember = new User().setId(2L).setName("Biba");
        Chat existingChat = new Chat(1L, "chat", new HashSet<>(Set.of(client, existingMember)), new ArrayList<>());
        User newMember = new User().setId(3L).setName("Boba");
        Chat expectedToSave = new Chat().setId(1L).setChatName("chat").setUsers(
                Set.of(
                        client,
                        existingMember,
                        newMember)
        );
        Chat expectedChat = new Chat(1L, "chat", Set.of(client, existingMember, newMember), new ArrayList<>());

        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.of(existingChat));
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.of(newMember));
        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(client));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(chatRepository.save(expectedToSave)).thenReturn(expectedChat);

        assertEquals(expectedChat, chatService.addUserToChat(3L, 1L));
    }

    @Test
    @WithMockCustomUser
    void removeUserFromChat() {
        User client = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User existingMember = new User().setId(3L).setName("Biba");
        Chat existingChat = new Chat(1L, "chat", new HashSet<>(Set.of(client, existingMember)), null);

        Chat expectedChat = new Chat(1L, "chat", new HashSet<>(Set.of(client)), null);

        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.of(existingChat));
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.of(existingMember));
        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(client));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(chatRepository.save(existingChat)).thenReturn(expectedChat);

        assertEquals(Optional.of(expectedChat), chatService.removeUserFromChat(3L, 1L));
    }

    @Test
    @WithMockCustomUser
    void removeNonExistingUserFromChat() {
        Mockito.when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> chatService.removeUserFromChat(3L, 1L));
    }

    @Test
    @WithMockCustomUser
    void removeLastUserFromChat() {
        User client = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Chat existingChat = new Chat(1L, "chat", new HashSet<>(Set.of(client)), null);

        Mockito.when(chatRepository.findById(1L)).thenReturn(Optional.of(existingChat));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(client));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertEquals(Optional.empty(), chatService.removeUserFromChat(1L, 1L));
        Mockito.verify(chatRepository, Mockito.times(1)).delete(existingChat);
    }


}
