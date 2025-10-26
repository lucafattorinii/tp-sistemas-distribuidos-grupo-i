package com.empuje.reports.repository;

import com.empuje.reports.model.DonationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonationRecordRepository extends JpaRepository<DonationRecord, Long> {

    @Query("""
        SELECT dr FROM DonationRecord dr
        WHERE (:category IS NULL OR dr.category = :category)
        AND (:startDate IS NULL OR dr.createdDate >= :startDate)
        AND (:endDate IS NULL OR dr.createdDate <= :endDate)
        AND (:isDeleted IS NULL OR dr.isDeleted = :isDeleted)
        AND (:isReceived IS NULL OR dr.isReceived = :isReceived)
        AND (:organizationId IS NULL OR dr.organizationId = :organizationId)
        """)
    List<DonationRecord> findWithFilters(
        @Param("category") String category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("isDeleted") Boolean isDeleted,
        @Param("isReceived") Boolean isReceived,
        @Param("organizationId") String organizationId
    );

    @Query("""
        SELECT dr.category, dr.isDeleted, SUM(dr.quantity)
        FROM DonationRecord dr
        WHERE (:category IS NULL OR dr.category = :category)
        AND (:startDate IS NULL OR dr.createdDate >= :startDate)
        AND (:endDate IS NULL OR dr.createdDate <= :endDate)
        AND (:isDeleted IS NULL OR dr.isDeleted = :isDeleted)
        AND (:isReceived IS NULL OR dr.isReceived = :isReceived)
        AND (:organizationId IS NULL OR dr.organizationId = :organizationId)
        GROUP BY dr.category, dr.isDeleted
        """)
    List<Object[]> findGroupedByCategoryAndDeleted(
        @Param("category") String category,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        @Param("isDeleted") Boolean isDeleted,
        @Param("isReceived") Boolean isReceived,
        @Param("organizationId") String organizationId
    );
}
