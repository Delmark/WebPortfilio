package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.PlacesOfWorkDto;
import com.delmark.portfoilo.models.PlacesOfWork;
import java.util.List;

public interface WorkplacesService {
    List<PlacesOfWork> getAllWorkplaces(Long portfolioId);
    PlacesOfWork getWorkplaceById(Long workId);
    PlacesOfWork addWorkplaceToPortfolio(Long portfolioId, PlacesOfWorkDto dto);
    PlacesOfWork editWorkplaceInfo(Long id, PlacesOfWorkDto dto);
    void deleteWorkplace(Long id);
}
