package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.ProjectsDto;
import com.delmark.portfoilo.models.portfoliodata.Projects;
import com.delmark.portfoilo.service.interfaces.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/projects")
public class ProjectsController {

    ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Projects>> getAllProjects(@RequestParam("portfolioId") Long id) {
        return ResponseEntity.ok(projectService.getAllProjects(id));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Projects> getProjectById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping
    public ResponseEntity<Projects> addProjectToPortfolio(@RequestParam("portfolioId") Long id, @RequestBody @Valid ProjectsDto dto) {
        return ResponseEntity.ok(projectService.addProjectToPortfolio(id, dto));
    }

    @PutMapping ResponseEntity<Projects> editProject(@RequestParam("projectId") Long id, @RequestBody @Valid ProjectsDto dto) {
        return ResponseEntity.ok(projectService.editProject(id,dto));
    }

    @DeleteMapping ResponseEntity<Void> deleteProject(@RequestParam("projectId") Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }
}
