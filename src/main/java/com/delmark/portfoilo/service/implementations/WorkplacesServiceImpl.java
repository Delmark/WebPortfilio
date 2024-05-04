package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.exceptions.NoSuchPortfolioException;
import com.delmark.portfoilo.exceptions.NoSuchWorkException;
import com.delmark.portfoilo.exceptions.WorkplaceAlreadyExistsInPortfolioException;
import com.delmark.portfoilo.models.DTO.PlacesOfWorkDto;
import com.delmark.portfoilo.models.PlacesOfWork;
import com.delmark.portfoilo.models.Portfolio;
import com.delmark.portfoilo.models.Role;
import com.delmark.portfoilo.models.User;
import com.delmark.portfoilo.repository.PlacesOfWorkRepository;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.service.interfaces.WorkplacesService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WorkplacesServiceImpl implements WorkplacesService {

    PlacesOfWorkRepository placesOfWorkRepository;
    PortfolioRepository portfolioRepository;
    RolesRepository rolesRepository;

    @Override
    public List<PlacesOfWork> getAllWorkplaces(Long portfolioId) {
        return placesOfWorkRepository.findAllByPortfolioId(portfolioId);
    }

    @Override
    public PlacesOfWork getWorkplaceById(Long workId) {
        return placesOfWorkRepository.findById(workId).orElseThrow(NoSuchWorkException::new);
    }

    @Override
    public PlacesOfWork addWorkplaceToPortfolio(Long portfolioId, PlacesOfWorkDto dto) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NoSuchPortfolioException::new);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!(portfolio.getUser().getId().equals(user.getId()) && user.getRoles().contains(adminRole))) {
            throw new AccessDeniedException("Пользователь не имеет доступа к данному портфолио");
        }

        if (placesOfWorkRepository.findByWorkplaceNameAndPortfolio(dto.getWorkplaceName(), portfolio).isPresent()) {
            throw new WorkplaceAlreadyExistsInPortfolioException();
        }

        PlacesOfWork workplace = new PlacesOfWork().
                setWorkplaceName(dto.getWorkplaceName()).
                setWorkplaceDesc(dto.getWorkplaceDesc()).
                setPortfolio(portfolio);

        return placesOfWorkRepository.save(workplace);
    }

    @Override
    public PlacesOfWork editWorkplaceInfo(Long id, PlacesOfWorkDto dto) {
        PlacesOfWork workplace = placesOfWorkRepository.findById(id).orElseThrow(NoSuchWorkException::new);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Portfolio portfolio = workplace.getPortfolio();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!(portfolio.getUser().getId().equals(user.getId()) && user.getRoles().contains(adminRole))) {
            throw new AccessDeniedException("Нет доступа");
        }


        if (dto.getWorkplaceName() != null) {
            workplace.setWorkplaceName(dto.getWorkplaceName());
        }
        if (dto.getWorkplaceDesc() != null) {
            workplace.setWorkplaceDesc(dto.getWorkplaceDesc());
        }

        return placesOfWorkRepository.save(workplace);
    }

    @Override
    public void deleteWorkplace(Long id) {
        PlacesOfWork workplace = placesOfWorkRepository.findById(id).orElseThrow(NoSuchWorkException::new);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio portfolio = workplace.getPortfolio();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!(portfolio.getUser().getId().equals(user.getId()) && user.getRoles().contains(adminRole))) {
            throw new AccessDeniedException("Нет доступа");
        }

        placesOfWorkRepository.delete(workplace);
    }
}
