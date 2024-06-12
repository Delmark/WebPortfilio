package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.exceptions.response.NoSuchPortfolioException;
import com.delmark.portfoilo.exceptions.response.NoSuchWorkException;
import com.delmark.portfoilo.exceptions.response.WorkplaceAlreadyExistsInPortfolioException;
import com.delmark.portfoilo.controller.requests.WorkplaceRequest;
import com.delmark.portfoilo.models.DTO.WorkplacesStatsProjection;
import com.delmark.portfoilo.models.portfolio.Workplace;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.WorkplacesRepository;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.delmark.portfoilo.service.interfaces.WorkplacesService;
import com.delmark.portfoilo.utils.CustomMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WorkplacesServiceImpl implements WorkplacesService {

    private WorkplacesRepository workplacesRepository;
    private PortfolioRepository portfolioRepository;
    private RolesRepository rolesRepository;
    private UserService userService;
    public CustomMapper mapper;

    @Override
    public List<Workplace> getAllWorkplaces(Long portfolioId) {
        if (portfolioRepository.existsById(portfolioId)) {
            return workplacesRepository.findAllByPortfolioId(portfolioId);
        }
        else {
            throw new NoSuchPortfolioException();
        }
    }

    @Override
    public Workplace getWorkplaceById(Long workId) {
        return workplacesRepository.findById(workId).orElseThrow(NoSuchWorkException::new);
    }

    @Override
    public Workplace addWorkplaceToPortfolio(Long portfolioId, WorkplaceRequest dto) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NoSuchPortfolioException::new);
        User user = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!portfolio.getUser().getId().equals(user.getId()) && !user.getRoles().contains(adminRole)) {
            throw new AccessDeniedException("Пользователь не имеет доступа к данному портфолио");
        }

        if (workplacesRepository.findByWorkplaceNameAndPortfolio(dto.getWorkplaceName(), portfolio).isPresent()) {
            throw new WorkplaceAlreadyExistsInPortfolioException();
        }

        Workplace workplace = new Workplace().
                setWorkplaceName(dto.getWorkplaceName()).
                setWorkplaceDesc(dto.getWorkplaceDesc()).
                setFireDate(dto.getFireDate()).
                setHireDate(dto.getHireDate()).
                setPost(dto.getPost()).
                setPortfolio(portfolio);

        return workplacesRepository.save(workplace);
    }

    @Override
    public Workplace editWorkplaceInfo(Long id, WorkplaceRequest dto) {
        Workplace workplace = workplacesRepository.findById(id).orElseThrow(NoSuchWorkException::new);
        User user = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

        Portfolio portfolio = workplace.getPortfolio();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!portfolio.getUser().getId().equals(user.getId()) && !user.getRoles().contains(adminRole)) {
            throw new AccessDeniedException("Нет доступа");
        }

        mapper.updateWorkplaceFromDTO(dto, workplace);

        return workplacesRepository.save(workplace);
    }

    @Override
    public void deleteWorkplace(Long id) {
        Workplace workplace = workplacesRepository.findById(id).orElseThrow(NoSuchWorkException::new);
        User user = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());
        Portfolio portfolio = workplace.getPortfolio();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!portfolio.getUser().getId().equals(user.getId()) && !user.getRoles().contains(adminRole)) {
            throw new AccessDeniedException("Нет доступа");
        }

        workplacesRepository.delete(workplace);
    }

    @Override
    public List<WorkplacesStatsProjection> getStatistics() {
        return workplacesRepository.getStatistics();
    }
}
