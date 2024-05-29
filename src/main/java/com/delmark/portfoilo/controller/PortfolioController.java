package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.exceptions.ErrorResponse;
import com.delmark.portfoilo.models.DTO.PortfolioDto;
import com.delmark.portfoilo.models.Portfolio;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@AllArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    // GET
    @GetMapping("/{username}")
    public ResponseEntity<Portfolio> getPortfolioByUser(@PathVariable("username") String username) {
        return ResponseEntity.ok(portfolioService.getPortfolioByUser(username));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Portfolio> getPortfolioById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(portfolioService.getPortfolio(id));
    }

    // POST

    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody @Valid PortfolioDto portfolio) {
        return ResponseEntity.ok(portfolioService.portfolioCreation(portfolio));
    }

    // PUT

    @PutMapping("/tech")
    public ResponseEntity<Portfolio> addTechToPortfolio(@RequestParam("portId") Long portfolioID,
                                                        @RequestParam("techId") Long techID) {
        return ResponseEntity.ok(portfolioService.addTechToPortfolio(portfolioID,techID));
    }


    @PutMapping
    public ResponseEntity<Portfolio> editPortfolio(@RequestParam Long id, @RequestBody @Valid PortfolioDto portfolio) {
        return ResponseEntity.ok(portfolioService.portfolioEdit(id, portfolio));
    }

    // DELETE


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.ok().build();
    }
}
