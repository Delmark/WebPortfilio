package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.controller.requests.PortfolioRequest;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.portfolio.Techs;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.repository.TechRepository;
import com.delmark.portfoilo.repository.UserRepository;
import com.delmark.portfoilo.service.interfaces.TokenService;
import com.delmark.portfoilo.utils.CustomMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PortfolioControllerTest {
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
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Настройка тестовой БД

        Role userRole = rolesRepository.findByAuthority("USER").get();

        User normalUser = new User(null,
                "Delmark",
                passwordEncoder.encode("123"),
                true,
                new HashSet<>(List.of(userRole))
        );
        normalUser = userRepository.save(normalUser);

        // Добавление существующего технологии


        Techs techs = new Techs(null, "Java", "Java is a programming language and computing platform.");
        techRepository.save(techs);

        // Добавление существующего портфолио

        Portfolio existingPortfolio = new Portfolio()
                .setName("Name")
                .setMiddleName("Middle name")
                .setSurname("Surname")
                .setAboutUser("About")
                .setEducation("Education")
                .setUser(normalUser);
        portfolioRepository.save(existingPortfolio);
    }

    @Test
    void getPortfolioByUser() throws Exception {
        Portfolio expected = portfolioRepository.findById(1L).get();

        mockMvc.perform(MockMvcRequestBuilders.get( "/api/portfolio/Delmark")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void getPortfolioByID() throws Exception {
        Portfolio expected = portfolioRepository.findById(1L).get();

        mockMvc.perform(MockMvcRequestBuilders.get( "/api/portfolio/id/1")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void getPortfolioByWrongID() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/portfolio/id/100")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getPortfolioByWrongName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/portfolio/Delma")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void addTechTopPortfolio() throws Exception {
        Portfolio expected = portfolioRepository.findById(1L).get();
        expected.getTechses().add(techRepository.findById(1L).get());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio/tech?portId=1&techId=1")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void createNewPortfolio() throws Exception {

        // Убираем портфолио у существующего тестового пользователя
        // Это намного легче, чем создавать нового пользователя
        Portfolio portfolio = portfolioRepository.findById(1L).get();
        portfolio.setUser(null);
        portfolioRepository.save(portfolio);

        PortfolioRequest dto = new PortfolioRequest("TestUser",
                "TestMiddleName",
                "TestSurname",
                "TestAboutUser",
                "TestEducation",
                "testgmail@gmail.com",
                null,
                null
        );

        Portfolio expected = mapper.toEntity(dto);
        expected.setId(2L);
        expected.setUser(userRepository.findByUsername("Delmark").get());
        expected.setTechses(new HashSet<>());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expected)));
    }

    // В этом тесте важно держать в уме, что пользователь и портфолио привязанное к нему уже существуют.
    @Test
    void createNewPortfolioWhenItExists() throws Exception {
        PortfolioRequest dto = new PortfolioRequest("TestUser",
                "TestMiddleName",
                "TestSurname",
                "TestAboutUser",
                "TestEducation",
                "testgmail@gmail.com",
                null,
                null
        );

        mockMvc.perform(MockMvcRequestBuilders.post("/api/portfolio")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void editPortfolio() throws Exception {
        PortfolioRequest dto = new PortfolioRequest("TestUser",
                "TestMiddleName",
                "TestSurname",
                "TestAboutUser",
                "TestEducation",
                "testgmail@gmail.com",
                null,
                null
        );

        Portfolio expectedPortfolio = portfolioRepository.findById(1L).get();
        mapper.updatePortfolioFromDTO(dto, expectedPortfolio);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio?id=1")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
                .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                {
                    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                    return grantedAuthoritiesConverter.convert(jwt);
                })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(
                        objectMapper.writeValueAsString(expectedPortfolio))
                );
    }

    @Test
    void editNonExistingPortfolio() throws Exception {
        PortfolioRequest dto = new PortfolioRequest("TestUser",
                "TestMiddleName",
                "TestSurname",
                "TestAboutUser",
                "TestEducation",
                "testgmail@gmail.com",
                null,
                null
        );


        mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio?id=2")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void editOtherUserPortfolio() throws Exception {
        PortfolioRequest dto = new PortfolioRequest("TestUser",
                "TestMiddleName",
                "TestSurname",
                "TestAboutUser",
                "TestEducation",
                "testgmail@gmail.com",
                null,
                null
        );

        User user = new User(null, "OtherUser", "123", true, new HashSet<>(List.of(rolesRepository.findByAuthority("USER").get())));
        portfolioRepository.save(mapper.toEntity(dto).setUser(userRepository.saveAndFlush(user)));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/portfolio?id=2")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deletePortfolio() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/portfolio/1")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteNonExistingPortfolio() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/portfolio/2")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteOtherUserPortfolio() throws Exception {

        // Заменяем владельца превого портфолио на нового пользователя
        User user = new User(null, "OtherUser", "123", true, new HashSet<>(List.of(rolesRepository.findByAuthority("USER").get())));
        user = userRepository.save(user);
        portfolioRepository.save(portfolioRepository.findById(1L).get().setUser(user));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/portfolio/1")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void deleteTechFromPortfolio() throws Exception {
        Techs java = techRepository.findByTechName("Java").get();
        Portfolio existingPortfolio = portfolioRepository.findById(1L).get();

        existingPortfolio.getTechses().add(java);
        portfolioRepository.save(existingPortfolio);
        existingPortfolio.getTechses().remove(java);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/portfolio/tech?portId=1&techId=1")
                .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                {
                    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                    return grantedAuthoritiesConverter.convert(jwt);
                })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(existingPortfolio)));
    }

    @Test
    void deleteNonExistingTechFromPortfolio() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/portfolio/tech?portId=1&techId=1")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteTechFromOtherUserPortfolio() throws Exception {
        Portfolio existingPortfolio = portfolioRepository.findById(1L).get();
        User user = new User(null, "OtherUser", "123", true, new HashSet<>(List.of(rolesRepository.findByAuthority("USER").get())));
        userRepository.save(user);
        portfolioRepository.save(existingPortfolio.setUser(user));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/portfolio/tech?portId=1&techId=1")
                .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                {
                    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                    return grantedAuthoritiesConverter.convert(jwt);
                })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
