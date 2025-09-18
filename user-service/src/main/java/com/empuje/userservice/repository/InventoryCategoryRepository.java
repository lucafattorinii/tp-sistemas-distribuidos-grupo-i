package com.empuje.userservice.repository;

import com.empuje.userservice.model.InventoryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, Integer> {
    Optional<InventoryCategory> findByName(String name);
    boolean existsByName(String name);
}
