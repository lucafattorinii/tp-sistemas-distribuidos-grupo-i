package com.empuje.userservice.repository;

import com.empuje.userservice.model.InventoryItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByName(String name);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.name LIKE %:query% OR i.description LIKE %:query%")
    Page<InventoryItem> search(@Param("query") String query, Pageable pageable);
    
    List<InventoryItem> findByCategoryIdAndActiveTrue(Integer categoryId);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.currentQuantity <= i.minimumQuantity AND i.active = true")
    List<InventoryItem> findLowStockItems();
    
    @Query("SELECT i FROM InventoryItem i WHERE i.deleted = false AND i.active = true")
    Page<InventoryItem> findAllActive(Pageable pageable);
}
