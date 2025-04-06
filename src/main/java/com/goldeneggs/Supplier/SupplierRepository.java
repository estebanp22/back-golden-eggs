package com.goldeneggs.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for supplier management.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
