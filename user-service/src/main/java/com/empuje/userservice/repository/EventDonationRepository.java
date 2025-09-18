package com.empuje.userservice.repository;

import com.empuje.userservice.model.Event;
import com.empuje.userservice.model.EventDonation;
import com.empuje.userservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface EventDonationRepository extends JpaRepository<EventDonation, Long> {
    
    Page<EventDonation> findByEvent(Event event, Pageable pageable);
    
    Page<EventDonation> findByDonorUser(User donor, Pageable pageable);
    
    @Query("SELECT SUM(ed.estimatedValue) FROM EventDonation ed WHERE ed.event = :event AND ed.status = 'COMPLETED'")
    BigDecimal sumDonationsByEvent(@Param("event") Event event);
    
    @Query("SELECT COUNT(ed) FROM EventDonation ed WHERE ed.event = :event")
    long countByEvent(@Param("event") Event event);
    
    @Query("SELECT ed FROM EventDonation ed WHERE ed.event = :event AND ed.donationType = :donationType")
    Page<EventDonation> findByEventAndDonationType(
        @Param("event") Event event,
        @Param("donationType") String donationType,
        Pageable pageable
    );
    
    @Query("SELECT ed FROM EventDonation ed WHERE ed.event = :event AND ed.status = :status")
    Page<EventDonation> findByEventAndStatus(
        @Param("event") Event event,
        @Param("status") String status,
        Pageable pageable
    );
    
    @Query("SELECT ed FROM EventDonation ed WHERE ed.createdAt BETWEEN :startDate AND :endDate")
    Page<EventDonation> findDonationsBetweenDates(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    @Query("SELECT ed FROM EventDonation ed WHERE ed.receiptRequired = true AND ed.receiptNumber IS NULL")
    Page<EventDonation> findDonationsRequiringReceipt(Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(ed.estimatedValue), 0) FROM EventDonation ed WHERE ed.event = :event")
    BigDecimal getTotalDonationAmountForEvent(@Param("event") Event event);
}
