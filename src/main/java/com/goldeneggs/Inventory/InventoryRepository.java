package com.goldeneggs.Inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Inventory management.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
