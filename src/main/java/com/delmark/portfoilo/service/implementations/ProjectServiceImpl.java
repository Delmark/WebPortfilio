package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.models.DTO.ProjectsDTO;
import com.delmark.portfoilo.exceptions.response.NoSuchPortfolioException;
import com.delmark.portfoilo.exceptions.response.NoSuchProjectException;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.portfolio.Projects;
import com.delmark.portfoilo.models.user.Role;
import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.repository.PortfolioRepository;
import com.delmark.portfoilo.repository.ProjectsRepository;
import com.delmark.portfoilo.repository.RolesRepository;
import com.delmark.portfoilo.service.interfaces.ProjectService;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.delmark.portfoilo.utils.CustomMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private ProjectsRepository projectsRepository;
    private PortfolioRepository portfolioRepository;
    private UserService userService;
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
    public Projects addProjectToPortfolio(Long portfolioId, ProjectsDTO dto) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElseThrow(NoSuchPortfolioException::new);
        User user = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

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
    public Projects editProject(Long id, ProjectsDTO dto) {
        Projects project = projectsRepository.findById(id).orElseThrow(NoSuchProjectException::new);
        Portfolio portfolio = project.getPortfolio();
        User user = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());

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
        User user = userService.getUserByAuth(SecurityContextHolder.getContext().getAuthentication());
        Portfolio portfolio = project.getPortfolio();

        Role adminRole = rolesRepository.findByAuthority("ADMIN").get();

        if (!user.getId().equals(portfolio.getUser().getId()) && !user.getAuthorities().contains(adminRole)) {
            throw new AccessDeniedException("Нет доступа");
        }

        projectsRepository.delete(project);
    }
}
