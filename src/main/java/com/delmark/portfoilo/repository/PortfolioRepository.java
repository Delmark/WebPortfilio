package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.portfoliodata.Portfolio;
import com.delmark.portfoilo.models.userdata.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Optional<Portfolio> findByUser(User user);

    boolean existsByUser(User user);
}
