package com.empuje.userservice.repository;

import com.empuje.userservice.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventStatusRepository extends JpaRepository<EventStatus, Integer> {
    Optional<EventStatus> findByName(String name);
    boolean existsByName(String name);
    
    @Query("SELECT es FROM EventStatus es WHERE es.active = true")
    List<EventStatus> findAllActive();
}
