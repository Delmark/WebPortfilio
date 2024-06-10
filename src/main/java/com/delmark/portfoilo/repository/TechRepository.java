package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.DTO.TechStatsProjection;
import com.delmark.portfoilo.models.portfoliodata.Techs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechRepository extends JpaRepository<Techs, Long>, PagingAndSortingRepository<Techs, Long> {
    Optional<Techs> findByTechName(String name);
    @Query(value = "SELECT tech.technology_name, count(*) FROM \"technologies\" tech JOIN \"portfolio_techses\" pt ON tech.id = pt.techses_id LEFT JOIN \"portfolio\" port ON port.id = pt.portfolio_id GROUP BY tech.technology_name ORDER BY COUNT(*) DESC", nativeQuery = true)
    List<TechStatsProjection> getTechStatistics();
}
