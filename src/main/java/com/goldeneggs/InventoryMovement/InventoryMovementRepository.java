package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Egg.Egg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Inventory management.
 */
@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
    Optional<InventoryMovement> findTopByEggOrderByMovementDateDesc(Egg egg);

}
