package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.PlacesOfWorkDto;
import com.delmark.portfoilo.models.PlacesOfWork;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/workPlaces")
public class PlacesOfWorkController {

    @GetMapping
    public ResponseEntity<Page<PlacesOfWork>> getAllWorkplaces(@RequestParam("portfolioId") Long id) {
        return null;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PlacesOfWork> getWorkplaceById(@PathVariable("id") Long id) {
        return null;
    }

    @PostMapping
    public ResponseEntity<PlacesOfWork> addWorkplaceToPortfolio(@RequestParam("portfolioId") Long id, PlacesOfWorkDto dto, Principal principal) {
        return null;
    }

    @PutMapping
    public ResponseEntity<PlacesOfWork> editWorkplaceInfo(@RequestParam("workplaceId") Long id, PlacesOfWorkDto dto, Principal principal) {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteWorkplaceInfo(@RequestParam("workplaceId") Long id, Principal principal) {
        return null;
    }

}
