package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.DTO.WorkplacesStatsDTO;
import com.delmark.portfoilo.models.Workplace;
import com.delmark.portfoilo.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkplacesRepository extends JpaRepository<Workplace, Long> {
    List<Workplace> findAllByPortfolioId(Long portfolioId);
    Optional<Workplace> findByWorkplaceNameAndPortfolio(String name, Portfolio portfolio);

    @Query("SELECT new WorkplacesStatsDTO(p.workplaceName, count(*)) FROM workplace p GROUP BY p.workplaceName ORDER BY count(*) DESC")
    List<WorkplacesStatsDTO> getStatistics();
}
