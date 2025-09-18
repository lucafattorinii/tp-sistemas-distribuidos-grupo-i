package com.empuje.userservice.repository;

import com.empuje.userservice.model.InventoryItem;
import com.empuje.userservice.model.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    
    List<InventoryMovement> findByItemIdOrderByMovementDateDesc(Long itemId);
    
    @Query("SELECT m FROM InventoryMovement m WHERE m.item = :item AND m.movementDate BETWEEN :startDate AND :endDate ORDER BY m.movementDate DESC")
    List<InventoryMovement> findByItemAndDateRange(
        @Param("item") InventoryItem item,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT m FROM InventoryMovement m WHERE m.referenceType = :referenceType AND m.referenceId = :referenceId")
    List<InventoryMovement> findByReference(
        @Param("referenceType") String referenceType,
        @Param("referenceId") Long referenceId
    );
    
    @Query("SELECT m FROM InventoryMovement m WHERE m.movementDate BETWEEN :startDate AND :endDate")
    Page<InventoryMovement> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    @Query("SELECT m FROM InventoryMovement m WHERE m.item.id = :itemId ORDER BY m.movementDate DESC")
    Page<InventoryMovement> findLatestMovementsByItemId(
        @Param("itemId") Long itemId,
        Pageable pageable
    );
}
