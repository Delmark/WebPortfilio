package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.ProjectsDto;
import com.delmark.portfoilo.models.Projects;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

    @GetMapping
    public ResponseEntity<Page<Projects>> getAllProjects(@RequestParam("portfolioId") Long id) {
        return null;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Projects> getProjectById(@PathVariable("id") Long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<Projects> addProjectToPortfolio(@RequestParam("portfolioId") Long id, @RequestBody ProjectsDto dto, Principal principal) {
        return null;
    }

    @PutMapping ResponseEntity<Projects> editProject(@RequestParam("projectId") Long id, @RequestBody ProjectsDto dto, Principal principal) {
        return null;
    }

    @DeleteMapping ResponseEntity<Void> deleteProject(@RequestParam("projectId") Long id, Principal principal) {
        return null;
    }
}
