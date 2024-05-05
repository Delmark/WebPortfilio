package com.delmark.portfoilo.service;

import com.delmark.portfoilo.exceptions.NoSuchPortfolioException;
import com.delmark.portfoilo.exceptions.NoSuchWorkException;
import com.delmark.portfoilo.models.*;
import com.delmark.portfoilo.models.DTO.PlacesOfWorkDto;
import com.delmark.portfoilo.repository.PlacesOfWorkRepository;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class WorkplacesTest {

    private final Date date = new Date();

    private static PlacesOfWorkRepository placesOfWorkRepository = Mockito.mock(PlacesOfWorkRepository.class);;

    private static RolesRepository rolesRepository = Mockito.mock(RolesRepository.class);

    private PortfolioRepository portfolioRepository = Mockito.mock(PortfolioRepository.class);

    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CustomMapper mapper = new CustomMapperImpl();
    private static final UserService userService = new UserServiceImpl(userRepository, rolesRepository, passwordEncoder);

    private final WorkplacesService workplacesService = new WorkplacesServiceImpl(placesOfWorkRepository, portfolioRepository, rolesRepository, userService, mapper);

    @Test
    @WithMockCustomUser
    void getAllWorkplaces() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio existingPortfolio = new Portfolio()
                .setId(1L)
                .setUser(user)
                .setName("test");

        List<PlacesOfWork> existingWorkplaces = List.of(
                new PlacesOfWork(1L, existingPortfolio, "test", "test", "test", date, date),
                new PlacesOfWork(2L, existingPortfolio, "test", "test", "test", date, date)
        );

        Mockito.when(portfolioRepository.existsById(1L)).thenReturn(true);
        Mockito.when(placesOfWorkRepository.findAllByPortfolioId(1L)).thenReturn(existingWorkplaces);

        assertEquals(existingWorkplaces, workplacesService.getAllWorkplaces(1L));
    }

    @Test
    void getAllWorkplacesByNonExistingId() {
        Mockito.when(placesOfWorkRepository.existsById(1L)).thenReturn(false);

        assertThrows(NoSuchPortfolioException.class, () -> workplacesService.getAllWorkplaces(1L));
    }

    @Test
    void getWorkplaceById() {
        PlacesOfWork existingWorkplace = new PlacesOfWork(1L, null, "test", "test", "test", date, date);

        Mockito.when(placesOfWorkRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));

        assertEquals(existingWorkplace, workplacesService.getWorkplaceById(1L));
    }

    @Test
    void getWorkplaceByNonExistingId() {
        Mockito.when(placesOfWorkRepository.findById(1L)).thenReturn(Optional.empty());

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

        PlacesOfWorkDto dto = new PlacesOfWorkDto("test", "test", "test", date, date);

        PlacesOfWork savedWorkplace = new PlacesOfWork(null, existingPortfolio, "test", "test", "test", date, date);
        PlacesOfWork expectedWorkplace = new PlacesOfWork(1L, existingPortfolio, "test", "test", "test", date, date);

        Mockito.when(portfolioRepository.findById(1L)).thenReturn(Optional.ofNullable(existingPortfolio));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(placesOfWorkRepository.save(savedWorkplace)).thenReturn(expectedWorkplace);


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

        PlacesOfWorkDto dto = new PlacesOfWorkDto("test", "test", "test", date, date);

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

        PlacesOfWorkDto dto = new PlacesOfWorkDto("test", "test", "test", date, date);

        PlacesOfWork existingWorkplace = new PlacesOfWork(1L, existingPortfolio, "none", "none", "none", date, date);
        PlacesOfWork expectedWorkplace = new PlacesOfWork(1L, existingPortfolio, "test", "test", "test", date, date);

        Mockito.when(placesOfWorkRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        Mockito.when(placesOfWorkRepository.save(expectedWorkplace)).thenReturn(expectedWorkplace);


        assertEquals(expectedWorkplace, workplacesService.editWorkplaceInfo(1L, dto));
    }

    @Test
    void updateWorkplaceForNonExistingWork() {
        Mockito.when(placesOfWorkRepository.findById(1L)).thenReturn(Optional.empty());
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
        PlacesOfWork existingWorkplace = new PlacesOfWork(1L, existingPortfolio, "none", "none", "none", date, date);

        Mockito.when(placesOfWorkRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));
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
        PlacesOfWork existingWorkplace = new PlacesOfWork(1L, existingPortfolio, "none", "none", "none", date, date);

        Mockito.when(placesOfWorkRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));
        workplacesService.deleteWorkplace(1L);
        Mockito.verify(placesOfWorkRepository, Mockito.times(1)).delete(existingWorkplace);
    }

    @Test
    void deleteWorkplaceForNonExistingWork() {
        Mockito.when(placesOfWorkRepository.findById(1L)).thenReturn(Optional.empty());
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
        PlacesOfWork existingWorkplace = new PlacesOfWork(1L, existingPortfolio, "none", "none", "none", date, date);

        Mockito.when(placesOfWorkRepository.findById(1L)).thenReturn(Optional.of(existingWorkplace));
        Mockito.when(rolesRepository.findByAuthority("ADMIN")).thenReturn(Optional.of(new Role(2L, "ADMIN")));

        assertThrows(AccessDeniedException.class, () -> workplacesService.deleteWorkplace(1L));
    }


}
