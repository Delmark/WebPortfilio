package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.models.DTO.PortfolioDTO;
import com.delmark.portfoilo.exceptions.response.*;
import com.delmark.portfoilo.models.messages.Comment;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.portfolio.Techs;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.*;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.delmark.portfoilo.utils.CustomMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private TechRepository techRepository;
    private final RolesRepository rolesRepository;
    public final CustomMapper customMapper;
    private final CommentRepository commentRepository;

    @Override
    public Portfolio getPortfolioByUser(String username) {
        if (userRepository.existsByUsername(username)) {
            Optional<Portfolio> portfolio = portfolioRepository.findByUser(userRepository.findByUsername(username).get());
            if (portfolio.isPresent()) {
                return portfolio.get();
            }
            else {
                throw new UserDoesNotHavePortfolioException();
            }
        }
        else {
            throw new UserNotFoundException();
        }
    }

    @Override
    public Portfolio getPortfolio(Long id) {
        Optional<Portfolio> portfolio = portfolioRepository.findById(id);
        if (portfolio.isPresent()) {
            return portfolio.get();
        }
        else {
            throw new NoSuchPortfolioException();
        }
    }

    @Override
    public Portfolio portfolioCreation(PortfolioDTO dto) {
        User sessionUser = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        if (portfolioRepository.findByUser(sessionUser).isPresent()) {
            throw new UserAlreadyHavePortfolioException();
        }

        Portfolio newPortfolio = new Portfolio()
                .setAboutUser(dto.getAboutUser())
                .setEducation(dto.getEducation())
                .setPhone(dto.getPhone())
                .setSiteUrl(dto.getSiteUrl())
                .setUser(sessionUser);

        return portfolioRepository.save(newPortfolio);
    }

    @Override
    public Portfolio addTechToPortfolio(Long portfolioId, Long techId) {
        Techs tech = techRepository.findById(techId).orElseThrow(NoSuchTechException::new);
        User sessionUser = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NoSuchPortfolioException::new);

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        // Проверка на доступ к портфолио
        if (!portfolio.getUser().getId().equals(sessionUser.getId()) && !sessionUser.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("У пользователя нет доступа к этму портфолио");
        }

        // Добавление технологии в портфолио, если её там ещё нет
        if (!portfolio.getTechses().contains(tech)) {
            portfolio.getTechses().add(tech);
            return portfolioRepository.save(portfolio);
        }

        throw new TechAlreadyInPortfolioException();
        }

    @Override
    public Portfolio portfolioEdit(Long id, PortfolioDTO dto) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(NoSuchPortfolioException::new);
        User sessionUser = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        // Проверка на доступ к портфолио
        if (!portfolio.getUser().getId().equals(sessionUser.getId()) && !sessionUser.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("У пользователя нет доступа к этму портфолио");
        }

        // Редактирование портфолио с помощью маппера
        customMapper.updatePortfolioFromDTO(dto, portfolio);
        return portfolioRepository.save(portfolio);
    }

    @Override
    public Portfolio removeTechFromPortfolio(Long portfolioId, Long techId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NoSuchPortfolioException::new);
        User sessionUser = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());
        Techs tech = techRepository.findById(techId).orElseThrow(NoSuchTechException::new);

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        // Проверка на доступ к портфолио
        if (!portfolio.getUser().getId().equals(sessionUser.getId()) && !sessionUser.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("У пользователя нет доступа к этму портфолио");
        }

        if (!portfolio.getTechses().contains(tech)) {
            throw new NoSuchTechInPortfolio();
        }

        portfolio.getTechses().remove(tech);

        return portfolioRepository.save(portfolio);
    }


    @Override
    public void deletePortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findById(id).orElseThrow(NoSuchPortfolioException::new);
        User sessionUser = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        // Проверка на доступ к портфолио
        if (!portfolio.getUser().getId().equals(sessionUser.getId()) && !sessionUser.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("У пользователя нет доступа к этму портфолио");
        }

        portfolioRepository.delete(portfolio);
    }

    @Override
    public boolean portfolioExistsByUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return portfolioRepository.findByUser(user).isPresent();
    }
}
