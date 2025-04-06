package com.goldeneggs.Egg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing Egg entities.
 */
@Repository
public interface EggRepository extends JpaRepository<Egg, Long> {
}
