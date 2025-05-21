package com.goldeneggs.OrderEgg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing order egg entities.
 */
@Repository
public interface OrderEggRepository extends JpaRepository<OrderEgg, Long> {
}
