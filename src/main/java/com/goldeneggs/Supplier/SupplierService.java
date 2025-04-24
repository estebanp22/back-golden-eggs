package com.goldeneggs.Supplier;

import java.util.List;

/**
 * Service interface for supplier operations.
 */
public interface SupplierService {

    /**
     * Retrieves all suppliers.
     * @return List of all suppliers.
     */
    List<Supplier> getAll();

    /**
     * Retrieves a supplier by its ID.
     * @param id Supplier ID.
     * @return Supplier if found, or throws ResourceNotFoundException.
     */
    Supplier get(Long id);

    /**
     * Saves a new supplier.
     * @param supplier Supplier data to save.
     * @return The saved supplier.
     */
    Supplier save(Supplier supplier);

    /**
     * Updates an existing supplier.
     * @param id Supplier ID.
     * @param updated Supplier data to update.
     * @return The updated supplier.
     */
    Supplier update(Long id, Supplier updated);

    /**
     * Deletes a supplier by its ID.
     * @param id Supplier ID to delete.
     */
    void delete(Long id);
}
