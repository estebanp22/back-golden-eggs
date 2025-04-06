package com.goldeneggs.Bill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing Bill entities.
 */
@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
}
