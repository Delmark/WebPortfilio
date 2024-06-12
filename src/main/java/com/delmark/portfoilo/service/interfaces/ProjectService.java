package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.controller.requests.ProjectsRequest;
import com.delmark.portfoilo.models.portfolio.Projects;

import java.util.List;

public interface ProjectService {
    List<Projects> getAllProjects(Long portfolioId);
    Projects getProjectById(Long id);
    Projects addProjectToPortfolio(Long portfolioId, ProjectsRequest dto);
    Projects editProject(Long id, ProjectsRequest dto);
    void deleteProject(Long id);
}
