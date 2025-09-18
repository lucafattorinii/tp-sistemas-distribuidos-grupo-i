package com.empuje.userservice.repository;

import com.empuje.userservice.model.Event;
import com.empuje.userservice.model.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    @Query("SELECT e FROM Event e WHERE e.startDatetime >= :startDate AND e.startDatetime < :endDate AND e.active = true")
    List<Event> findBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.active = true")
    Page<Event> findByStatus(
        @Param("status") EventStatus status,
        Pageable pageable
    );
    
    @Query("SELECT e FROM Event e WHERE e.isPublic = true AND e.active = true AND e.startDatetime > CURRENT_TIMESTAMP")
    Page<Event> findUpcomingPublicEvents(Pageable pageable);
    
    @Query("SELECT e FROM Event e JOIN e.participants p WHERE p.id = :userId")
    Page<Event> findEventsByParticipantId(
        @Param("userId") Long userId,
        Pageable pageable
    );
    
    @Query("SELECT e FROM Event e WHERE e.startDatetime > CURRENT_TIMESTAMP AND e.registrationDeadline > CURRENT_TIMESTAMP AND e.active = true")
    Page<Event> findOpenForRegistration(Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.startDatetime BETWEEN :startDate AND :endDate AND e.active = true")
    List<Event> findEventsInDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT e FROM Event e WHERE e.startDatetime < CURRENT_TIMESTAMP AND e.active = true")
    Page<Event> findPastEvents(Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.startDatetime > CURRENT_TIMESTAMP AND e.active = true")
    Page<Event> findUpcomingEvents(Pageable pageable);
}
