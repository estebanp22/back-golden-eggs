package com.goldeneggs.Supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for supplier management.
 */
@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    /**
     * Checks if a supplier with the given name and address exists.
     *
     * @param name    supplier's name
     * @param address supplier's address
     * @return true if exists, false otherwise
     */
    boolean existsByNameIgnoreCaseAndAddressIgnoreCase(String name, String address);
}
