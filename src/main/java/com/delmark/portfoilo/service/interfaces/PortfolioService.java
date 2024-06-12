package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.messages.Comment;
import com.delmark.portfoilo.controller.requests.PortfolioRequest;
import com.delmark.portfoilo.models.portfolio.Portfolio;

import java.util.Set;

public interface PortfolioService {
    Portfolio getPortfolioByUser(String username);
    Portfolio getPortfolio(Long id);
    Portfolio portfolioCreation(PortfolioRequest dto);
    Portfolio addTechToPortfolio(Long portfolioId, Long techId);
    Portfolio portfolioEdit(Long id, PortfolioRequest dto);
    Portfolio removeTechFromPortfolio(Long portfolioId, Long techId);
    Set<Comment> getPortfolioComments(Long portfolioId);
    boolean portfolioExistsByUser(String username);
    void deletePortfolio(Long id);
}
