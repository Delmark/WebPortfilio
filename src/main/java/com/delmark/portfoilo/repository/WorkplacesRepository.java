package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.DTO.WorkplacesStatsProjection;
import com.delmark.portfoilo.models.portfolio.Workplace;
import com.delmark.portfoilo.models.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkplacesRepository extends JpaRepository<Workplace, Long> {
    List<Workplace> findAllByPortfolioId(Long portfolioId);
    Optional<Workplace> findByWorkplaceNameAndPortfolio(String name, Portfolio portfolio);

    @Query(value = "SELECT p.workplace_name AS workplaceName, count(*) FROM workplace p GROUP BY p.workplace_name ORDER BY count(*) DESC", nativeQuery = true)
    List<WorkplacesStatsProjection> getStatistics();
}
