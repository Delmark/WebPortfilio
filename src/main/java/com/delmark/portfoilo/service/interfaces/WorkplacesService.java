package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.controller.requests.WorkplaceRequest;
import com.delmark.portfoilo.models.DTO.WorkplacesStatsProjection;
import com.delmark.portfoilo.models.portfolio.Workplace;

import java.util.List;

public interface WorkplacesService {
    List<Workplace> getAllWorkplaces(Long portfolioId);
    Workplace getWorkplaceById(Long workId);
    Workplace addWorkplaceToPortfolio(Long portfolioId, WorkplaceRequest dto);
    Workplace editWorkplaceInfo(Long id, WorkplaceRequest dto);
    void deleteWorkplace(Long id);
    List<WorkplacesStatsProjection> getStatistics();
}
