package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.PortfolioDto;
import com.delmark.portfoilo.models.Portfolio;

import java.security.Principal;
import java.util.Optional;

public interface PortfolioService {
    Portfolio getPortfolioByUser(String username);
    Portfolio getPortfolio(Long id);
    Portfolio portfolioCreation(PortfolioDto dto);
    Portfolio addTechToPortfolio(Long portfolioId, Long techId);
    Portfolio portfolioEdit(Long id, PortfolioDto dto);
    Portfolio removeTechFromPortfolio(Long portfolioId, Long techId);
    boolean portfolioExistsByUser(String username);
    void deletePortfolio(Long id);
}
