package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.PlacesOfWorkDto;
import com.delmark.portfoilo.models.PlacesOfWork;
import com.delmark.portfoilo.service.interfaces.WorkplacesService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/workPlaces")
public class WorkplacesController {

    WorkplacesService workplacesService;

    @GetMapping
    public ResponseEntity<List<PlacesOfWork>> getAllWorkplaces(@RequestParam("portfolioId") Long id) {
        return ResponseEntity.ok(workplacesService.getAllWorkplaces(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlacesOfWork> getWorkplaceById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(workplacesService.getWorkplaceById(id));
    }

    @PostMapping
    public ResponseEntity<PlacesOfWork> addWorkplaceToPortfolio(@RequestParam("portfolioId") Long id, PlacesOfWorkDto dto) {
        return ResponseEntity.ok(workplacesService.addWorkplaceToPortfolio(id, dto));
    }

    @PutMapping
    public ResponseEntity<PlacesOfWork> editWorkplaceInfo(@RequestParam("workplaceId") Long id, PlacesOfWorkDto dto) {
        return ResponseEntity.ok(workplacesService.editWorkplaceInfo(id, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteWorkplaceInfo(@RequestParam("workplaceId") Long id) {
        workplacesService.deleteWorkplace(id);
        return ResponseEntity.ok().build();
    }

}
