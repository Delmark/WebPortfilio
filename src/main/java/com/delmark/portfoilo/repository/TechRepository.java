package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.Techs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechRepository extends JpaRepository<Techs, Long> {
}
