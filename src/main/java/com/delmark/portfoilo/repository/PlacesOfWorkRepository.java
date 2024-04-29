package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.PlacesOfWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacesOfWorkRepository extends JpaRepository<PlacesOfWork, Long> {
}
