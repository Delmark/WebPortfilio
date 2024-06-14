package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.ChatCreationDTO;
import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.messages.Message;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.*;
import com.delmark.portfoilo.service.interfaces.ChatService;
import com.delmark.portfoilo.service.interfaces.TokenService;
import com.delmark.portfoilo.utils.CustomMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    CustomMapper mapper;
    @Autowired
    TechRepository techRepository;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    TokenService tokenService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private JwtAuthenticationConverter jwtAuthenticationConverter;
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {

        Role userRole = rolesRepository.findByAuthority("USER").get();

        User fisrtUser = new User().
                setUsername("Delmark")
                .setName("Delmark")
                .setSurname("Delmarkovich")
                .setPassword(passwordEncoder.encode("123456"))
                .setEmail("gmail@gmail.com")
                .setEnabled(true)
                .setRoles(Set.of(userRole));

        User secondUser = new User().
                setUsername("Kaban")
                .setName("Kaban")
                .setSurname("Kabanovich")
                .setPassword(passwordEncoder.encode("123456"))
                .setEmail("zavod@mail.ru")
                .setEnabled(true)
                .setRoles(Set.of(userRole));

        this.firstUser = userRepository.save(fisrtUser);
        this.secondUser = userRepository.save(secondUser);
    }

    @Test
    void createChat() throws Exception {
        Chat expectedChat = new Chat(1L, "Delmark - Kaban chat", Set.of(firstUser, secondUser), null);
        ChatCreationDTO chatCreationDTO = new ChatCreationDTO(null, Set.of(firstUser.getId(), secondUser.getId()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/chat")
                .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                {
                    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                    return grantedAuthoritiesConverter.convert(jwt);
                }))
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(chatCreationDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedChat)));
    }

    @Test
    void createChatWithOnePrincipal() throws Exception {
        ChatCreationDTO chatCreationDTO = new ChatCreationDTO(null, Set.of(firstUser.getId()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/chat")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities()))
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(chatCreationDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createChatWithoutClient() throws Exception {
        User thirdUser = new User().
                setUsername("Kaban1")
                .setName("Kaban")
                .setSurname("Kabanovich")
                .setPassword(passwordEncoder.encode("123456"))
                .setEmail("zavod@mail.ru")
                .setEnabled(true)
                .setRoles(Set.of(rolesRepository.findByAuthority("USER").get()));

        userRepository.save(thirdUser);
        ChatCreationDTO chatCreationDTO = new ChatCreationDTO(null, Set.of(secondUser.getId(), thirdUser.getId()));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/chat").
                with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(
                        jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())).
                contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(chatCreationDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void getAllChatsAsAdmin() throws Exception {
        Chat existingChat = chatRepository.save(new Chat(null, "Chat", Set.of(firstUser, secondUser), null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chat?page=0&size=10")
                .with(jwt().jwt(jwt -> jwt.subject("testAdmin").claim("authorities", "ADMIN")).authorities(
                        jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(new PageImpl<>(List.of(existingChat), PageRequest.of(0, 10), 1))));
    }

    @Test
    @Transactional
    void getAllChatsAsUser() throws Exception {
        chatRepository.save(new Chat(null, "Chat", Set.of(firstUser, secondUser), null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chat?page=0&size=10")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void getAllUserChatsAsAdmin() throws Exception {
        Chat existingChat = chatRepository.save(new Chat(null, "Chat", Set.of(firstUser, secondUser), null));
        Chat existingChat2 = chatRepository.save(new Chat(null, "Chat2", Set.of(firstUser, secondUser), null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chat/user/Delmark?page=0&size=10")
                        .with(jwt().jwt(jwt -> jwt.subject("testAdmin").claim("authorities", "ADMIN")).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(new PageImpl<>(List.of(existingChat, existingChat2), PageRequest.of(0, 10), 2))));
    }

    @Test
    @Transactional
    void getAllUserChatsAsUser() throws Exception {
        chatRepository.save(new Chat(null, "Chat", Set.of(firstUser, secondUser), null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chat/user/Delmark?page=0&size=10")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void getChatMessages() throws Exception {
        Chat chat = chatRepository.save(new Chat(null, "Chat", Set.of(firstUser, secondUser), null));
        Message chatMessage1 = new Message(null, "Hi", LocalDateTime.now(), firstUser, chat);
        Message chatMessage2 = new Message(null, "Hi, how are u", LocalDateTime.now().plusHours(1L), secondUser, chat);
        Message chatMessage3 = new Message(null, "Nice", LocalDateTime.now().plusHours(1L), firstUser, chat);
        messageRepository.saveAll(List.of(chatMessage1, chatMessage2, chatMessage3));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chat/1/messages?page=0&size=10")
                .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(
                        jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(
                        new PageImpl<>(List.of(chatMessage1, chatMessage2, chatMessage3), PageRequest.of(0, 10), 3)
                )));
    }

    @Test
    @Transactional
    void getChatMessagesAsStranger() throws Exception {
        User thirdUser = new User().
                setUsername("Kaban1")
                .setName("Kaban")
                .setSurname("Kabanovich")
                .setPassword(passwordEncoder.encode("123456"))
                .setEmail("zavod@mail.ru")
                .setEnabled(true)
                .setRoles(Set.of(rolesRepository.findByAuthority("USER").get()));

        userRepository.save(thirdUser);
        chatRepository.save(new Chat(null, "Chat", Set.of(firstUser, secondUser), null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chat/1/messages?page=0&size=10")
                        .with(jwt().jwt(jwt -> jwt.subject("Kaban1").claim("authorities", "USER")).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Transactional
    @Test
    void addUserToChat() throws Exception {
        Chat chat = chatRepository.save(new Chat(null, "Chat", new HashSet<>(Set.of(firstUser, secondUser)), null));
        User thirdUser = userRepository.save(new User().
                setUsername("Kaban1")
                .setName("Kaban")
                .setSurname("Kabanovich")
                .setPassword(passwordEncoder.encode("123456"))
                .setEmail("zavod@mail.ru")
                .setEnabled(true)
                .setRoles(Set.of(rolesRepository.findByAuthority("USER").get())));

        Chat expectedChat = new Chat(1L, "Chat", Set.of(firstUser, secondUser, thirdUser), null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/chat/1/user/4")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedChat), false));
    }

    @Transactional
    @Test
    void removeUserFromChat() throws Exception {
        Chat chat = chatRepository.save(new Chat(null, "Chat", new HashSet<>(Set.of(firstUser, secondUser)), null));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/chat/1/user/3")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Transactional
    @Test
    void removeLastUserFrom() throws Exception {
        Chat chat = chatRepository.save(new Chat(null, "Chat", new HashSet<>(Set.of(firstUser)), null));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/chat/1/user/2")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(Optional.empty(), chatRepository.findById(chat.getId()));
    }
}
