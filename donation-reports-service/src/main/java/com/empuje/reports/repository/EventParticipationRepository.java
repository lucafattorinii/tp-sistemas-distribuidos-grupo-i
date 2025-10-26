package com.empuje.reports.repository;

import com.empuje.reports.model.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {

    @Query("""
        SELECT ep FROM EventParticipation ep
        WHERE ep.eventDate >= :startDate
        AND ep.eventDate <= :endDate
        AND (:userId IS NULL OR ep.userId = :userId)
        AND (:hasDonationDistribution IS NULL OR ep.hasDonationDistribution = :hasDonationDistribution)
        AND (:organizationId IS NULL OR ep.organizationId = :organizationId)
        ORDER BY YEAR(ep.eventDate), MONTH(ep.eventDate), ep.eventDate
        """)
    List<EventParticipation> findWithFilters(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("userId") Long userId,
        @Param("hasDonationDistribution") Boolean hasDonationDistribution,
        @Param("organizationId") String organizationId
    );

    @Query("""
        SELECT YEAR(ep.eventDate), MONTH(ep.eventDate),
               ep.eventDate, ep.eventName, ep.eventDescription, ep.hasDonationDistribution
        FROM EventParticipation ep
        WHERE ep.eventDate >= :startDate
        AND ep.eventDate <= :endDate
        AND (:userId IS NULL OR ep.userId = :userId)
        AND (:hasDonationDistribution IS NULL OR ep.hasDonationDistribution = :hasDonationDistribution)
        AND (:organizationId IS NULL OR ep.organizationId = :organizationId)
        GROUP BY YEAR(ep.eventDate), MONTH(ep.eventDate), ep.eventDate, ep.eventName, ep.eventDescription, ep.hasDonationDistribution
        ORDER BY YEAR(ep.eventDate), MONTH(ep.eventDate), ep.eventDate
        """)
    List<Object[]> findGroupedByMonth(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("userId") Long userId,
        @Param("hasDonationDistribution") Boolean hasDonationDistribution,
        @Param("organizationId") String organizationId
    );
}
