package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.PortfolioDTO;
import com.delmark.portfoilo.models.messages.Comment;
import com.delmark.portfoilo.models.portfolio.Portfolio;

import java.util.Set;

public interface PortfolioService {
    Portfolio getPortfolioByUser(String username);
    Portfolio getPortfolio(Long id);
    Portfolio portfolioCreation(PortfolioDTO dto);
    Portfolio addTechToPortfolio(Long portfolioId, Long techId);
    Portfolio portfolioEdit(Long id, PortfolioDTO dto);
    Portfolio removeTechFromPortfolio(Long portfolioId, Long techId);
    boolean portfolioExistsByUser(String username);
    void deletePortfolio(Long id);
}
