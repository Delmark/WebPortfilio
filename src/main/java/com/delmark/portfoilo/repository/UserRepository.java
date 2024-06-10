package com.delmark.portfoilo.repository;

import com.delmark.portfoilo.models.userdata.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    @Query("FROM app_user u JOIN portfolio p ON p.user.id = u.id")
    List<User> getUsersWithExistingPortfolio();
}
