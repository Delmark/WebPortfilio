package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.exceptions.NoSuchPortfolioException;
import com.delmark.portfoilo.exceptions.NoSuchProjectException;
import com.delmark.portfoilo.models.DTO.ProjectsDto;
import com.delmark.portfoilo.models.Portfolio;
import com.delmark.portfoilo.models.Projects;
import com.delmark.portfoilo.models.Role;
import com.delmark.portfoilo.models.User;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.ProjectsRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.service.interfaces.ProjectService;
import com.delmark.portfoilo.utils.CustomMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private ProjectsRepository projectsRepository;
    private PortfolioRepository portfolioRepository;
    public CustomMapper mapper;
    private final RolesRepository rolesRepository;

    @Override
    public List<Projects> getAllProjects(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NoSuchPortfolioException::new);
        return projectsRepository.findAllByPortfolio(portfolio);
    }

    @Override
    public Projects getProjectById(Long id) {
        return projectsRepository.findById(id).orElseThrow(NoSuchProjectException::new);
    }

    @Override
    public Projects addProjectToPortfolio(Long portfolioId, ProjectsDto dto) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NoSuchPortfolioException::new);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!user.getId().equals(portfolio.getUser().getId()) && !user.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("Нет доступа");
        }

        Projects projects = new Projects()
                .setProjectName(dto.getProjectName())
                .setProjectDesc(dto.getProjectDesc())
                .setProjectLink(dto.getProjectLink())
                .setPortfolio(portfolio);

        return projectsRepository.save(projects);
    }

    @Override
    public Projects editProject(Long id, ProjectsDto dto) {
        Projects project = projectsRepository.findById(id).orElseThrow(NoSuchProjectException::new);
        Portfolio portfolio = project.getPortfolio();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!user.getId().equals(portfolio.getUser().getId()) && !user.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("Нет доступа");
        }

        mapper.updateProjectFromDTO(dto, project);

        return project;
    }

    @Override
    public void deleteProject(Long id) {
        Projects project = projectsRepository.findById(id).orElseThrow(NoSuchProjectException::new);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Portfolio portfolio = project.getPortfolio();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!user.getId().equals(portfolio.getUser().getId()) && !user.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("Нет доступа");
        }

        projectsRepository.delete(project);
    }
}
