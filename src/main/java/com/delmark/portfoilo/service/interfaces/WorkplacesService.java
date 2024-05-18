package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.PlacesOfWorkDto;
import com.delmark.portfoilo.models.DTO.WorkplacesStatsDTO;
import com.delmark.portfoilo.models.Workplace;

import java.util.List;

public interface WorkplacesService {
    List<Workplace> getAllWorkplaces(Long portfolioId);
    Workplace getWorkplaceById(Long workId);
    Workplace addWorkplaceToPortfolio(Long portfolioId, PlacesOfWorkDto dto);
    Workplace editWorkplaceInfo(Long id, PlacesOfWorkDto dto);
    void deleteWorkplace(Long id);
    List<WorkplacesStatsDTO> getStatistics();
}
