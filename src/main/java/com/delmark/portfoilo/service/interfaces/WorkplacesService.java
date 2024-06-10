package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.WorkplaceDto;
import com.delmark.portfoilo.models.DTO.WorkplacesStatsDTO;
import com.delmark.portfoilo.models.portfoliodata.Workplace;

import java.util.List;

public interface WorkplacesService {
    List<Workplace> getAllWorkplaces(Long portfolioId);
    Workplace getWorkplaceById(Long workId);
    Workplace addWorkplaceToPortfolio(Long portfolioId, WorkplaceDto dto);
    Workplace editWorkplaceInfo(Long id, WorkplaceDto dto);
    void deleteWorkplace(Long id);
    List<WorkplacesStatsDTO> getStatistics();
}
