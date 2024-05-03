package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.Techs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechRepository extends JpaRepository<Techs, Long>, PagingAndSortingRepository<Techs, Long> {
    Optional<Techs> findByTechName(String name);
}
