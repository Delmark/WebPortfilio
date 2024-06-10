package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.ProjectsDto;
import com.delmark.portfoilo.models.portfoliodata.Projects;

import java.util.List;

public interface ProjectService {
    List<Projects> getAllProjects(Long portfolioId);
    Projects getProjectById(Long id);
    Projects addProjectToPortfolio(Long portfolioId, ProjectsDto dto);
    Projects editProject(Long id, ProjectsDto dto);
    void deleteProject(Long id);
}
