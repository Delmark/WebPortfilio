package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.WorkplaceDto;
import com.delmark.portfoilo.models.Workplace;
import com.delmark.portfoilo.service.interfaces.WorkplacesService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/workPlaces")
public class WorkplacesController {

    WorkplacesService workplacesService;

    @GetMapping
    public ResponseEntity<List<Workplace>> getAllWorkplaces(@RequestParam("portfolioId") Long id) {
        return ResponseEntity.ok(workplacesService.getAllWorkplaces(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workplace> getWorkplaceById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(workplacesService.getWorkplaceById(id));
    }

    @PostMapping
    public ResponseEntity<Workplace> addWorkplaceToPortfolio(@RequestParam("portfolioId") Long id, @RequestBody @Valid WorkplaceDto dto) {
        return ResponseEntity.ok(workplacesService.addWorkplaceToPortfolio(id, dto));
    }

    @PutMapping
    public ResponseEntity<Workplace> editWorkplaceInfo(@RequestParam("workplaceId") Long id, @RequestBody @Valid WorkplaceDto dto) {
        return ResponseEntity.ok(workplacesService.editWorkplaceInfo(id, dto));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteWorkplaceInfo(@RequestParam("workplaceId") Long id) {
        workplacesService.deleteWorkplace(id);
        return ResponseEntity.ok().build();
    }

}
