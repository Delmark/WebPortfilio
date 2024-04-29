package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolesRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthority(String authority);
}
