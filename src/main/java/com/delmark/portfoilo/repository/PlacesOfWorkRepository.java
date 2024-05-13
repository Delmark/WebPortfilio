package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.DTO.WorkplacesStatsDTO;
import com.delmark.portfoilo.models.PlacesOfWork;
import com.delmark.portfoilo.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacesOfWorkRepository extends JpaRepository<PlacesOfWork, Long> {
    List<PlacesOfWork> findAllByPortfolioId(Long portfolioId);
    Optional<PlacesOfWork> findByWorkplaceNameAndPortfolio(String name, Portfolio portfolio);

    @Query("SELECT new WorkplacesStatsDTO(p.workplaceName, count(*)) FROM Places_Of_Work p GROUP BY p.workplaceName ORDER BY count(*) DESC")
    List<WorkplacesStatsDTO> getStatistics();
}
