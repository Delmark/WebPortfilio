package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.messages.Message;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MessagesControllerTest {

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
    private Chat mainChat;

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

        Chat mainChat = new Chat()
                .setChatName("Test Chat")
                .setUsers(Set.of(this.firstUser, this.secondUser));

        this.mainChat = chatRepository.save(mainChat);
    }

    @Test
    @Transactional
    void getMessageById() throws Exception {
        Message message = messageRepository.save(
                new Message()
                        .setChat(mainChat)
                        .setSender(firstUser)
                        .setMessage("Test Message")
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/messages/" + message.getId())
                .with(jwt().jwt( jwt -> jwt.subject("Delmark").claim("authorities", "USER").build()).authorities(
                        jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities()
                )))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(message)));
    }

    @Test
    @Transactional
    void createMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages?chatId=1&message=Test")
                .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER").build()).authorities(
                        jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities()
                )))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        assertEquals(messageRepository.findAll().size(), 1);
    }

    @Test
    @Transactional
    void editMessage() throws Exception {
        Message message = messageRepository.save(
                new Message()
                        .setChat(mainChat)
                        .setSender(firstUser)
                        .setMessage("Test Message")
        );
        Message editedMessage = new Message().
                setId(1L).
                setMessage("Edited").
                setChat(mainChat).
                setCreatedAt(message.getCreatedAt()).
                setSender(firstUser);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/messages/1?message=Edited")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER").build()).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities()
                        )))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(editedMessage)));
    }

    @Test
    @Transactional
    void deleteMessage() throws Exception {
        Message message = messageRepository.save(
                new Message()
                        .setChat(mainChat)
                        .setSender(firstUser)
                        .setMessage("Test Message")
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/messages/1")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER").build()).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities()
                        )))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertEquals(Optional.empty(), messageRepository.findById(message.getId()));
    }

    @Test
    @Transactional
    void deleteOtherUserMessage() throws Exception {
        Message message = messageRepository.save(
                new Message()
                        .setChat(mainChat)
                        .setSender(secondUser)
                        .setMessage("Test Message")
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/messages/1")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER").build()).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities()
                        )))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void EditOtherUserMessage() throws Exception {
        Message message = messageRepository.save(
                new Message()
                        .setChat(mainChat)
                        .setSender(secondUser)
                        .setMessage("Test Message")
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/messages/1?message=Edited")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER").build()).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities()
                        )))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void createMessageInForeignChat() throws Exception {
        mainChat.setUsers(Set.of(secondUser));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/messages?chatId=1&message=Test")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER").build()).authorities(
                                jwt -> jwtAuthenticationConverter.convert(jwt).getAuthorities()
                        )))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
        assertEquals(messageRepository.findAll().size(), 0);
    }
}
