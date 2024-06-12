package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.portfolio.Portfolio;
import com.delmark.portfoilo.models.portfolio.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long>, PagingAndSortingRepository<Projects, Long> {
    List<Projects> findAllByPortfolio(Portfolio portfolio);
}
