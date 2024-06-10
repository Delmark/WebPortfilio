package com.delmark.portfoilo.service;

import com.delmark.portfoilo.exceptions.response.NoSuchPortfolioException;
import com.delmark.portfoilo.exceptions.response.NoSuchWorkException;
import com.delmark.portfoilo.models.DTO.WorkplaceDto;
import com.delmark.portfoilo.models.portfoliodata.Portfolio;
import com.delmark.portfoilo.models.portfoliodata.Workplace;
import com.delmark.portfoilo.models.userdata.Role;
import com.delmark.portfoilo.models.userdata.User;
import com.delmark.portfoilo.repository.WorkplacesRepository;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.repository.UserRepository;
import com.delmark.portfoilo.service.implementations.UserServiceImpl;
import com.delmark.portfoilo.service.implementations.WorkplacesServiceImpl;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.delmark.portfoilo.service.interfaces.WorkplacesService;
import com.delmark.portfoilo.utils.CustomMapper;
import com.delmark.portfoilo.utils.CustomMapperImpl;
import com.delmark.portfoilo.utils.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class WorkplacesTest {

    private final Date date = new Date();

    private static WorkplacesRepository workplacesRepository = Mockito.mock(WorkplacesRepository.class);;

    private static RolesRepository rolesRepository = Mockito.mock(RolesRepository.class);

    private PortfolioRepository portfolioRepository = Mockito.mock(PortfolioRepository.class);

    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CustomMapper mapper = new CustomMapperImpl();
    private final UserService userService = new UserServiceImpl(userRepository, rolesRepository, passwordEncoder, portfolioRepository);

    private final WorkplacesService workplacesService = new WorkplacesServiceImpl(workplacesRepository, portfolioRepository, rolesRepository, userService, mapper);

    @Test
    @WithMockCustomUser
    void getAllWorkplaces() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setUser(user)
                .setName("test");

        List<Workplace> existingWorkplaces = List.of(
                new Workplace(1L, existingPortfolio, "test", "test", "test", date, date),
                new Workplace(2L, existingPortfolio, "test", "test", "test", date, date)
        );

        Mockito.when(portfolioRepository.existsById(1L)).thenReturn(true);
        Mockito.when(workplacesRepository.findAllByPortfolioId(1L)).thenReturn(existingWorkplaces);

        assertEquals(existingWorkplaces, workplacesService.getAllWorkplaces(1L));
    }

    @Test
    void getAllWorkplacesByNonExistingId() {
        Mockito.when(workplacesRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchPortfolioException.class, () -> workplacesService.getAllWorkplaces(1L));
    }

    @Test
    void getWorkplaceById() {
        Workplace existingWorkplace = new Workplace(1L, null, "test", "test", "test", date, date);

        Mockito.when(workplacesRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));

        assertEquals(existingWorkplace, workplacesService.getWorkplaceById(1L));
    }

    @Test
    void getWorkplaceByNonExistingId() {
        Mockito.when(workplacesRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchWorkException.class, () -> workplacesService.getWorkplaceById(1L));
    }

    @Test
    @WithMockCustomUser
    void createWorkplace() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio()
                .setName("Test Portfolio")
                .setId(1L)
                .setUser(user);

        WorkplaceDto dto = new WorkplaceDto("test", "test", "test", date, date);

        Workplace savedWorkplace = new Workplace(null, existingPortfolio, "test", "test", "test", date, date);
        Workplace expectedWorkplace = new Workplace(1L, existingPortfolio, "test", "test", "test", date, date);

        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.ofNullable(existingPortfolio));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(workplacesRepository.save(savedWorkplace)).thenReturn(expectedWorkplace);


        assertEquals(expectedWorkplace, workplacesService.addWorkplaceToPortfolio(1L, dto));
    }

    @Test
    @WithMockCustomUser
    void createWorkplaceForNonExistingPortfolio() {
        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchPortfolioException.class, () -> workplacesService.addWorkplaceToPortfolio(1L, null));
    }


    @Test
    @WithMockCustomUser
    void createWorkplaceForOtherUser() {
        User otherUser = new User().setUsername("test").setPassword("geniusPass").setEnabled(true).setId(2L);
        Portfolio existingPortfolio = new Portfolio()
                .setName("Test Portfolio")
                .setId(1L)
                .setUser(otherUser);

        WorkplaceDto dto = new WorkplaceDto("test", "test", "test", date, date);

        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.ofNullable(existingPortfolio));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));


        assertThrows(AccessDeniedException.class, () -> workplacesService.addWorkplaceToPortfolio(1L, dto));
    }

    @Test
    @WithMockCustomUser
    void updateWorkplace() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio()
                .setName("Test Portfolio")
                .setId(1L)
                .setUser(user);

        WorkplaceDto dto = new WorkplaceDto("test", "test", "test", date, date);

        Workplace existingWorkplace = new Workplace(1L, existingPortfolio, "none", "none", "none", date, date);
        Workplace expectedWorkplace = new Workplace(1L, existingPortfolio, "test", "test", "test", date, date);

        Mockito.when(workplacesRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(workplacesRepository.save(expectedWorkplace)).thenReturn(expectedWorkplace);


        assertEquals(expectedWorkplace, workplacesService.editWorkplaceInfo(1L, dto));
    }

    @Test
    void updateWorkplaceForNonExistingWork() {
        Mockito.when(workplacesRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchWorkException.class, () -> workplacesService.editWorkplaceInfo(1L, null));
    }

    @Test
    @WithMockCustomUser
    void updateWorkplaceForOtherUser() {
        User otherUser = new User().setUsername("test").setPassword("testPass").setEnabled(true).setId(2L);
        Portfolio existingPortfolio = new Portfolio()
                .setName("Test Portfolio")
                .setId(1L)
                .setUser(otherUser);
        Workplace existingWorkplace = new Workplace(1L, existingPortfolio, "none", "none", "none", date, date);

        Mockito.when(workplacesRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(AccessDeniedException.class, () -> workplacesService.editWorkplaceInfo(1L, null));
    }


    @Test
    @WithMockCustomUser
    void deleteWorkplace() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio()
                .setName("Test Portfolio")
                .setId(1L)
                .setUser(user);
        Workplace existingWorkplace = new Workplace(1L, existingPortfolio, "none", "none", "none", date, date);

        Mockito.when(workplacesRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        workplacesService.deleteWorkplace(1L);
        Mockito.verify(workplacesRepository, Mockito.times(1)).delete(existingWorkplace);
    }

    @Test
    void deleteWorkplaceForNonExistingWork() {
        Mockito.when(workplacesRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchWorkException.class, () -> workplacesService.deleteWorkplace(1L));
    }

    @Test
    @WithMockCustomUser
    void deleteWorkplaceForOtherUser() {
        User otherUser = new User().setUsername("test").setPassword("testPass").setEnabled(true).setId(2L);
        Portfolio existingPortfolio = new Portfolio()
                .setName("Test Portfolio")
                .setId(1L)
                .setUser(otherUser);
        Workplace existingWorkplace = new Workplace(1L, existingPortfolio, "none", "none", "none", date, date);

        Mockito.when(workplacesRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(AccessDeniedException.class, () -> workplacesService.deleteWorkplace(1L));
    }


}
