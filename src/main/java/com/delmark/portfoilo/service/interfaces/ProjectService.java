package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.ProjectsDTO;
import com.delmark.portfoilo.models.portfolio.Projects;

import java.util.List;

public interface ProjectService {
    List<Projects> getAllProjects(Long portfolioId);
    Projects getProjectById(Long id);
    Projects addProjectToPortfolio(Long portfolioId, ProjectsDTO dto);
    Projects editProject(Long id, ProjectsDTO dto);
    void deleteProject(Long id);
}
