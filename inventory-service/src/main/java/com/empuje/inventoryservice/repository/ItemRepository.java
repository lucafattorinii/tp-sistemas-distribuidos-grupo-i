package com.empuje.inventoryservice.repository;

import com.empuje.inventoryservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByDeletedFalseOrderByCreatedAtDesc();
}
