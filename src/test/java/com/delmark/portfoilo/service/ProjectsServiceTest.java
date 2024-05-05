package com.delmark.portfoilo.service;

import com.delmark.portfoilo.exceptions.NoSuchPortfolioException;
import com.delmark.portfoilo.exceptions.NoSuchProjectException;
import com.delmark.portfoilo.models.DTO.ProjectsDto;
import com.delmark.portfoilo.models.Portfolio;
import com.delmark.portfoilo.models.Projects;
import com.delmark.portfoilo.models.Role;
import com.delmark.portfoilo.models.User;
import com.delmark.portfoilo.repository.*;
import com.delmark.portfoilo.service.implementations.ProjectServiceImpl;
import com.delmark.portfoilo.service.implementations.UserServiceImpl;
import com.delmark.portfoilo.service.interfaces.ProjectService;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.delmark.portfoilo.utils.CustomMapper;
import com.delmark.portfoilo.utils.CustomMapperImpl;
import com.delmark.portfoilo.utils.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class ProjectsServiceTest {
    private static final ProjectsRepository projectsRepository = Mockito.mock(ProjectsRepository.class);
    private static final PortfolioRepository portfolioRepository = Mockito.mock(PortfolioRepository.class);
    private static final RolesRepository rolesRepository = Mockito.mock(RolesRepository.class);
    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static final CustomMapper customMapper = new CustomMapperImpl();
    private static final UserService userService = new UserServiceImpl(userRepository, rolesRepository, passwordEncoder);

    ProjectService projectService = new ProjectServiceImpl(projectsRepository, portfolioRepository, userService, customMapper, rolesRepository);

    @Test
    @DisplayName("Получение всех проектов")
    void getAllProjects() {
        User existingUser = new User(1L, "Existing User", "123", true, new HashSet<>(List.of(new Role(1L, "USER"))));
        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setName("Average Portfolio")
                .setAboutUser("About user")
                .setUser(existingUser)
                .setTechses(new HashSet<>());

        List<Projects> expectedProjects = List.of(
                new Projects(1L, existingPortfolio, "1", "1", "1"),
                new Projects(1L, existingPortfolio, "2", "2", "2"),
                new Projects(1L, existingPortfolio, "3", "3", "3")
        );

        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.ofNullable(existingPortfolio));
        Mockito.when(projectsRepository.findAllByPortfolio(existingPortfolio)).thenReturn(expectedProjects);

        assertEquals(expectedProjects, projectService.getAllProjects(1L));
    }

    @Test
    @DisplayName("Попытка получить проекты у несуществующего портфолио")
    void getAllProjectsWithNonExistingPortfolio() {
        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchPortfolioException.class, () -> projectService.getAllProjects(1L));
    }

    @Test
    @DisplayName("Получение проекта по ID")
    void getProjectById() {
        User existingUser = new User(1L, "Existing User", "123", true, new HashSet<>(List.of(new Role(1L, "USER"))));
        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setName("Average Portfolio")
                .setAboutUser("About user")
                .setUser(existingUser)
                .setTechses(new HashSet<>());

        Projects existingProject = new Projects(1L, existingPortfolio, "1", "1", "1");

        Mockito.when(projectsRepository.findById(1L)).thenReturn(Optional.of(existingProject));

        assertEquals(existingProject, projectService.getProjectById(1L));
    }

    @DisplayName("Попытка получения проекта по несуществующему ID")
    @Test
    void getProjectByNonExistingId() {
        Mockito.when(projectsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchProjectException.class, () -> projectService.getProjectById(1L));
    }

    @DisplayName("Добавление проекта в портфолио")
    @Test
    @WithMockCustomUser
    void addProjectToPortfolio() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setName("Average Portfolio")
                .setAboutUser("About user")
                .setUser(user)
                .setTechses(new HashSet<>());

        ProjectsDto dto = new ProjectsDto("Project Name", "Project Desc", "Url");
        Projects savedProject = new Projects()
                .setProjectName(dto.getProjectName())
                .setProjectDesc(dto.getProjectDesc())
                .setProjectLink(dto.getProjectLink())
                .setPortfolio(existingPortfolio);

        Projects expectedProject = new Projects()
                .setId(1L)
                .setProjectName(dto.getProjectName())
                .setProjectDesc(dto.getProjectDesc())
                .setProjectLink(dto.getProjectLink())
                .setPortfolio(existingPortfolio);


        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.ofNullable(existingPortfolio));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(projectsRepository.save(savedProject)).thenReturn(expectedProject);

        assertEquals(expectedProject, projectService.addProjectToPortfolio(1L, dto));
    }

    @DisplayName("Попытка добавить проект в несуществующий портфолио")
    @Test
    void addProjectToNonExistingPortfolio() {
        Mockito.when(projectsRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchProjectException.class, () -> projectService.getProjectById(1L));
    }

    @DisplayName("Попытка добавить проект в чужое портфолио")
    @Test
    @WithMockCustomUser
    void addProjectToOtherUserPortfolio() {
        User user = new User(2L, "123", "345", true, new HashSet<>(List.of(new Role(1L, "USER"))));

        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setName("Average Portfolio")
                .setAboutUser("About user")
                .setUser(user)
                .setTechses(new HashSet<>());

        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.ofNullable(existingPortfolio));

        assertThrows(AccessDeniedException.class, () -> projectService.addProjectToPortfolio(1L, new ProjectsDto("1", "1", "1")));
    }

    @DisplayName("Редактирование проекта")
    @Test
    @WithMockCustomUser
    void editProject() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setName("Average Portfolio")
                .setAboutUser("About user")
                .setUser(user)
                .setTechses(new HashSet<>());

        Projects existingProject = new Projects(1L, existingPortfolio, "Proj", "Porj Des", null);

        ProjectsDto dto = new ProjectsDto("Project Name", "Project Desc", "Url");

        Projects expectedProject = new Projects()
                .setId(1L)
                .setProjectName(dto.getProjectName())
                .setProjectDesc(dto.getProjectDesc())
                .setProjectLink(dto.getProjectLink())
                .setPortfolio(existingPortfolio);


        Mockito.when(projectsRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(projectsRepository.save(expectedProject)).thenReturn(expectedProject);

        assertEquals(expectedProject, projectService.editProject(1L, dto));
    }

    @DisplayName("Попытка редактировать несуществующий проект")
    @Test
    void editNonExistingProject() {
        Mockito.when(projectsRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchProjectException.class, () -> projectService.editProject(1L, new ProjectsDto("1", "1", "1")));
    }

    @DisplayName("Попытка отредактировать чужой проект")
    @Test
    @WithMockCustomUser
    void editOtherUserProject() {
        User user = new User(2L, "123", "345", true, new HashSet<>(List.of(new Role(1L, "USER"))));

        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setName("Average Portfolio")
                .setAboutUser("About user")
                .setUser(user)
                .setTechses(new HashSet<>());

        Projects existingProject = new Projects(1L, existingPortfolio, "Proj", "Porj Des", null);

        Mockito.when(projectsRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(AccessDeniedException.class, () -> projectService.editProject(1L, new ProjectsDto("1", "1", "1")));
    }

    @Test
    @DisplayName("Удаление проекта")
    @WithMockCustomUser
    void deleteProject() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setName("Average Portfolio")
                .setAboutUser("About user")
                .setUser(user)
                .setTechses(new HashSet<>());

        Projects existingProject = new Projects(1L, existingPortfolio, "Proj", "Porj Des", null);

        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(projectsRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        projectService.deleteProject(1L);
        Mockito.verify(projectsRepository, Mockito.times(1)).delete(existingProject);
    }

    @Test
    @DisplayName("Попытка удалить чужой проект")
    @WithMockCustomUser
    void deleteOtherUserProject() {
        User user = new User(2L, "123", "345", true, new HashSet<>(List.of(new Role(1L, "USER"))));

        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setName("Average Portfolio")
                .setAboutUser("About user")
                .setUser(user)
                .setTechses(new HashSet<>());

        Projects existingProject = new Projects(1L, existingPortfolio, "Proj", "Porj Des", null);

        Mockito.when(projectsRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(AccessDeniedException.class, () -> projectService.deleteProject(1L));
    }


}
