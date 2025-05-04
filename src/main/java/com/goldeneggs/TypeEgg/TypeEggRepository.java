package com.goldeneggs.TypeEgg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for type egg management.
 */
@Repository
public interface TypeEggRepository extends JpaRepository<TypeEgg, Long> {
}
