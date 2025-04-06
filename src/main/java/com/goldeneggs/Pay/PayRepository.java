package com.goldeneggs.Pay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing Pay entities.
 */
@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {
}

