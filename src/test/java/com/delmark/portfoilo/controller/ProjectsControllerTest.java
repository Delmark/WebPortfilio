package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.controller.requests.ProjectsRequest;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.portfolio.Projects;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.ProjectsRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.repository.UserRepository;
import com.delmark.portfoilo.utils.CustomMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProjectsControllerTest {
    @Autowired
    private CustomMapper mapper;
    @Autowired
    PortfolioRepository portfolioRepository;
    @Autowired
    private ProjectsRepository projectsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RolesRepository rolesRepository;
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
    void getAllProjects() throws Exception {
        // Создание проектов прикреплённых к портфолио
        Projects project1 = projectsRepository.save(new Projects(null, portfolioRepository.findById(1L).get(), "Project 1", "Desc 1", "Link"));
        Projects project2 = projectsRepository.save(new Projects(null, portfolioRepository.findById(1L).get(), "Project 2", "Desc 2", "Link"));
        List<Projects> projectsList = List.of(project1, project2);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects?portfolioId=1")
                .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                {
                    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                    return grantedAuthoritiesConverter.convert(jwt);
                })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(projectsList)));
    }

    @Test
    void getProjectById() throws Exception {
        Projects expectedProject = projectsRepository.save(new Projects(null, portfolioRepository.findById(1L).get(), "Project 1", "Desc 1", "Link"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects/id/1")
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedProject)));
    }

    @Test
    void getProjectByNonExistingId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/projects/id/2")
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
    void addProjectToPortfolio() throws Exception {
        Portfolio portfolio = portfolioRepository.findById(1L).get();

        ProjectsRequest dto = new ProjectsRequest("Project 1", "Project Desc", "https://projectlink.com");

        Projects expectedProject = mapper.toEntity(dto);
        expectedProject.setId(1L).setPortfolio(portfolio);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/projects?portfolioId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                {
                    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                    grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                    grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                    return grantedAuthoritiesConverter.convert(jwt);
                })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedProject)));
    }

    @Test
    void addProjectToNonExistingPortfolio() throws Exception {
        ProjectsRequest dto = new ProjectsRequest("Project 1", "Project Desc", "https://projectlink.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/projects?portfolioId=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
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
    void addProjectToOtherUserPortfolio() throws Exception {
        ProjectsRequest dto = new ProjectsRequest("Project 1", "Project Desc", "https://projectlink.com");

        User otherUser = userRepository.save(
                new User(
                        null,
                        "Who",
                        passwordEncoder.encode("123456"),
                        true,
                        new HashSet<>(List.of(rolesRepository.findByAuthority("USER").get()))
                )
        );

        // Заменяем владельца портфолио с стандартного для другого
        Portfolio portfolio = portfolioRepository.findById(1L).get().setUser(otherUser);
        portfolioRepository.save(portfolio);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/projects?portfolioId=1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
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
    void editProject() throws Exception {
        Portfolio portfolio = portfolioRepository.findById(1L).get();
        Projects existingProject = projectsRepository.save(
                new Projects(null, portfolio, "Project 1", "Desc", "Link")
        );

        ProjectsRequest dto = new ProjectsRequest("Project 1 Edited", "Desc Edited", null);

        Projects expectedProject = new Projects(1L,portfolio,"Project 1 Edited", "Desc Edited", "Link");

        mockMvc.perform(MockMvcRequestBuilders.put("/api/projects?projectId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(jwt().jwt(jwt -> jwt.subject("Delmark").claim("authorities", "USER")).authorities(jwt ->
                        {
                            JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                            grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
                            grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
                            return grantedAuthoritiesConverter.convert(jwt);
                        })))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(expectedProject)));
    }

    @Test
    void editNonExistingProject() throws Exception {
        ProjectsRequest dto = new ProjectsRequest("Project 1 Edited", "Desc Edited", null);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/projects?projectId=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
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
    void deleteProject() throws Exception {
        Portfolio portfolio = portfolioRepository.findById(1L).get();
        Projects existingProject = projectsRepository.save(
                new Projects(null, portfolio, "Project 1", "Desc", "Link")
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/projects?projectId=1")
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
    void deleteOtherUserProject() throws Exception {
        User otherUser = userRepository.save(
                new User(
                        null,
                        "Who",
                        passwordEncoder.encode("123"),
                        true,
                        new HashSet<>(List.of(rolesRepository.findByAuthority("USER").get()))
                )
        );

        // Заменяем владельца портфолио с стандартного для другого
        Portfolio portfolio = portfolioRepository.findById(1L).get().setUser(otherUser);
        portfolioRepository.save(portfolio);

        Projects existingProject = projectsRepository.save(
                new Projects(null, portfolio, "Project 1", "Desc", "Link")
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/projects?projectId=1")
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
