package com.delmark.portfoilo.service;

import com.delmark.portfoilo.models.DTO.PortfolioDTO;
import com.delmark.portfoilo.exceptions.response.*;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.portfolio.Techs;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.repository.TechRepository;
import com.delmark.portfoilo.repository.UserRepository;
import com.delmark.portfoilo.service.implementations.PortfolioServiceImpl;
import com.delmark.portfoilo.service.implementations.UserServiceImpl;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class PortfolioServiceTest {

    private static final PortfolioRepository portfolioRepository = Mockito.mock(PortfolioRepository.class);
    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static TechRepository techRepository = Mockito.mock(TechRepository.class);
    private static final RolesRepository rolesRepository = Mockito.mock(RolesRepository.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final UserService userService = new UserServiceImpl(userRepository, rolesRepository, passwordEncoder, portfolioRepository);
    public static final CustomMapper customMapper = new CustomMapperImpl();

    PortfolioService portfolioService = new PortfolioServiceImpl(portfolioRepository, userService, userRepository, techRepository, rolesRepository, customMapper);

    @WithMockUser(username = "Delmark")
    @Test
    @DisplayName(value = "Получение портфолио по нику пользователя")
    void getPortfolioFromCorrectUser() {
        String username = "Delmark";
        User existingUser = new User(1L, "Delmark", "123", "Delmark", "Delmarkovich", null, "gmail@gmaii.com", null, true, new HashSet<>());
        Portfolio existingPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);
        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(existingUser));
        Mockito.when(portfolioRepository.findByUser(existingUser)).thenReturn(Optional.of(existingPortfolio));

        assertEquals(existingPortfolio, portfolioService.getPortfolioByUser(username));
    }

    @WithMockUser(username = "Delmark")
    @Test
    @DisplayName(value = "Попытка получить портфолио от пользователя, у которого оно отсутсвует")
    void getPortfolioFromUserWhoDoesNotHavePortfolio() {
        String username = "Delmark";
        User existingUser = new User(1L, "Delmark", "123", "Delmark", "Delmarkovich", null, "gmail@gmaii.com", null, true, new HashSet<>());

        Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);
        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.of(existingUser));
        Mockito.when(portfolioRepository.findByUser(existingUser)).thenReturn(Optional.empty());

        assertThrows(UserDoesNotHavePortfolioException.class, () -> portfolioService.getPortfolioByUser(username));
    }

    @WithMockUser(username = "Delmark")
    @Test
    @DisplayName(value = "Получение портфолио по нику несуществующего пользователя")
    void getPortfolioByUserWithNonExistingUser() {
        String inputUsername = "Delmark";
        Mockito.when(userRepository.existsByUsername(inputUsername)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> portfolioService.getPortfolioByUser(inputUsername));
    }

    @WithMockUser(username = "Delmark")
    @Test
    @DisplayName(value = "Получение портфолио по ID")
    void getPortfolioByExistingId() {
        User existingUser = new User(1L, "Delmark", "123", "Delmark", "Delmarkovich", null, "gmail@gmaii.com", null, true, new HashSet<>());
        Portfolio existingPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existingPortfolio));
        assertEquals(existingPortfolio, portfolioService.getPortfolio(1L));
    }

    @WithMockUser(username = "Delmark")
    @Test
    @DisplayName(value = "Попытка получить не существующее портфолио")
    void getPortfolioByNonExistingId() {
        Long inputID = 1L;
        Mockito.when(portfolioRepository.findById(inputID)).thenReturn(Optional.empty());
        assertThrows(NoSuchPortfolioException.class, () -> portfolioService.getPortfolio(inputID));
    }


    @Test
    @WithMockCustomUser()
    @DisplayName(value = "Создание портфолио")
    void createPortfolio() {
        User existingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio expectedPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        Portfolio preSavePortfolio = new Portfolio(
                null,
                existingUser,
                "About me",
                "YSTU",
                null,
                null,
                new LinkedHashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>()
        );


        PortfolioDTO dto = new PortfolioDTO(
                "About me",
                "YSTU",
                null,
                null
        );

        Mockito.when(portfolioRepository.findByUser(existingUser)).thenReturn(Optional.empty());
        Mockito.when(portfolioRepository.save(preSavePortfolio)).thenReturn(expectedPortfolio);

        Portfolio result = portfolioService.portfolioCreation(dto);

        assertEquals(expectedPortfolio, result);
    }

    @Test
    @WithMockCustomUser()
    @DisplayName(value = "Попытка создать портфолио, когда оно уже существует у пользователя")
    void createPortfolioWhenItExists() {
        User existingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        PortfolioDTO dto = new PortfolioDTO(
                "Test",
                "TestAbout",
                "TestEdu",
                "TestEmail@gmail.com"
        );

        Mockito.when(portfolioRepository.findByUser(existingUser)).thenReturn(Optional.of(existingPortfolio));

        assertThrows(UserAlreadyHavePortfolioException.class, () -> portfolioService.portfolioCreation(dto));
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Добавление технологии в портфолио")
    void addTechToPortfolio() {
        User existingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        Techs existingTech = new Techs(1L, "Java", "Java?");

        Portfolio expectedPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>())
                .setTechses(new HashSet<>(List.of(existingTech)));

        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.of(existingTech));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existingPortfolio));
        Mockito.when(portfolioRepository.save(expectedPortfolio)).thenReturn(expectedPortfolio);

        assertEquals(expectedPortfolio, portfolioService.addTechToPortfolio(1L, 1L));

    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Попытка добавить существующую технологию в портфолио")
    void addExistingTechToPortfolio() {
        User existingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Techs existingTech = new Techs(1L, "Java", "Java?");
        Portfolio existingPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>())
                .setTechses(new HashSet<>(List.of(existingTech)));

        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.of(existingTech));
        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existingPortfolio));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(TechAlreadyInPortfolioException.class, () -> portfolioService.addTechToPortfolio(1L,1L));
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Попытка добавить технологию в чужое портфолио")
    void addTechToOtherUserPortfolio() {
        User otherUser = new User(2L, "Delmarka", "123", "Delmark", "Delmarkovich", null, "gmail@gmaii.com", null, true, new HashSet<>());
        Techs existingTech = new Techs(1L, "Java", "Java?");
        Portfolio existingPortfolio = new Portfolio(1L, otherUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.of(existingTech));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existingPortfolio));

        assertThrows(AccessDeniedException.class, () -> portfolioService.addTechToPortfolio(1L,1L));
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Редактирвание портфолио")
    void portfolioEdit() {
        User existingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        Portfolio expectedPortfolio = new Portfolio(1L, existingUser, "TestAbout", "Schoolar", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        PortfolioDTO dto = new PortfolioDTO(
                "TestAbout",
                "Schoolar",
                null,
                null
        );

        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existingPortfolio));
        Mockito.when(portfolioRepository.save(expectedPortfolio)).thenReturn(expectedPortfolio);
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertEquals(expectedPortfolio, portfolioService.portfolioEdit(1L, dto));
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Попытка редактировать несуществующего портфолио")
    void editNonExistingPortfolio() {
        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchPortfolioException.class, () -> portfolioService.portfolioEdit(
                1L,
                new PortfolioDTO(
                "about",
                "schoolar",
                null,
                null
        )));
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Попытка редактировать чужое портфолио")
    void editOtherUserPortfolio() {
        User otherUser = new User(2L, "Delmarka", "123", "Delmark", "Delmarkovich", null, "gmail@gmaii.com", null, true, new HashSet<>());

        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Portfolio existingPortfolio = new Portfolio(1L, otherUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        PortfolioDTO dto = new PortfolioDTO(
                "TestAbout",
                "Schoolar",
                "TestEmail@gmail.com",
                null
        );


        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existingPortfolio));

        assertThrows(AccessDeniedException.class, () -> portfolioService.portfolioEdit(1L, dto));
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Удаление портфолио")
    void deletePortfolio() {
        User existingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existingPortfolio));
        portfolioService.deletePortfolio(1L);
        Mockito.verify(portfolioRepository, Mockito.times(1)).delete(existingPortfolio);
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Попытка удаления портфолио другого пользователя")
    void deleteOtherUserPortfolio() {
        User otherUser = new User(2L, "Delmarka", "123", "Delmark", "Delmarkovich", null, "gmail@gmaii.com", null, true, new HashSet<>());
        Portfolio existingPortfolio = new Portfolio(1L, otherUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.of(existingPortfolio));
        assertThrows(AccessDeniedException.class, () -> portfolioService.deletePortfolio(1L));
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Проверка существования портфолио у пользователя")
    void portfolioExistsByUser() {
        User existingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio(1L, existingUser, "About me", "YSTU", null, null, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        Mockito.when(userRepository.findByUsername(existingUser.getUsername())).thenReturn(Optional.of(existingUser));
        Mockito.when(portfolioRepository.findByUser(existingUser)).thenReturn(Optional.of(existingPortfolio));

        assertTrue(portfolioService.portfolioExistsByUser(existingUser.getUsername()));
    }

    @Test
    @WithMockCustomUser
    @DisplayName(value = "Отсутствие портфолио при проверке существования портфолио по пользователю")
    void nonExistingPortfolioByUser() {
        User existingUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(userRepository.findByUsername(existingUser.getUsername())).thenReturn(Optional.of(existingUser));
        Mockito.when(portfolioRepository.findByUser(existingUser)).thenReturn(Optional.empty());

        assertFalse(portfolioService.portfolioExistsByUser(existingUser.getUsername()));
    }

    @Test
    @DisplayName("Проверка существования портфолио по несуществующему пользователю")
    void checkPortfolioExistenceByNonExisingUser() {
        Mockito.when(userRepository.findByUsername("Delmark")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, ()-> portfolioService.portfolioExistsByUser("Delmark"));
    }
}
