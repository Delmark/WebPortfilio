package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.PlacesOfWork;
import com.delmark.portfoilo.models.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlacesOfWorkRepository extends JpaRepository<PlacesOfWork, Long> {
    List<PlacesOfWork> findAllByPortfolioId(Long portfolioId);
    Optional<PlacesOfWork> findByWorkplaceNameAndPortfolio(String name, Portfolio portfolio);
}
