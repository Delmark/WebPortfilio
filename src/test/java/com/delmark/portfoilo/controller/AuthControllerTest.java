package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.UserAuthDTO;
import com.delmark.portfoilo.models.DTO.UserRegDTO;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerNewUser() throws Exception {
        User expectedUser = new User().
                setId(2L).
                setUsername("Delmark").
                setPassword(passwordEncoder.encode("123456")).
                setName("Delmark").
                setSurname("Delmarkovich").
                setRoles(new HashSet<>(List.of(rolesRepository.findByAuthority("USER").get()))).
                setEmail("gmail@gmail.com").
                setEnabled(true);
        UserRegDTO regDTO = new UserRegDTO("Delmark", "123456", "Delmark", "Delmarkovich", null, "gmail@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void registerExistingUser() throws Exception {
        UserRegDTO regDTO = new UserRegDTO("testAdmin", "123", "Delmark", "Delmarkovich", null, "gmail@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getToken() throws Exception {
        UserAuthDTO userAuthDTO = new UserAuthDTO("testAdmin", "adminPass");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/getToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getTokenWithWrongUserData() throws Exception {
        UserAuthDTO userAuthDTO = new UserAuthDTO("test", "admass");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/getToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthDTO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
