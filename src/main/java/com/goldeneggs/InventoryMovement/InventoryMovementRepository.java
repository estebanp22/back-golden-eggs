package com.goldeneggs.InventoryMovement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Inventory management.
 */
@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
}
