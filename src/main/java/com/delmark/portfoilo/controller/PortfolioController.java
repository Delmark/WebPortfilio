package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.controller.requests.PortfolioRequest;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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
    public ResponseEntity<Portfolio> createPortfolio(@RequestBody @Valid PortfolioRequest portfolio) {
        return ResponseEntity.ok(portfolioService.portfolioCreation(portfolio));
    }

    // PUT

    @PutMapping("/tech")
    public ResponseEntity<Portfolio> addTechToPortfolio(@RequestParam("portId") Long portfolioID,
                                                        @RequestParam("techId") Long techID) {
        return ResponseEntity.ok(portfolioService.addTechToPortfolio(portfolioID,techID));
    }


    @PutMapping
    public ResponseEntity<Portfolio> editPortfolio(@RequestParam Long id, @RequestBody @Valid PortfolioRequest portfolio) {
        return ResponseEntity.ok(portfolioService.portfolioEdit(id, portfolio));
    }

    // DELETE

    @DeleteMapping("/tech")
    public ResponseEntity<Portfolio> removeTechFromPortfolio(
            @RequestParam("portId") Long portfolioId,
            @RequestParam("techId") Long techId
    ) {
        return ResponseEntity.ok(portfolioService.removeTechFromPortfolio(portfolioId, techId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable Long id) {
        portfolioService.deletePortfolio(id);
        return ResponseEntity.ok().build();
    }
}
