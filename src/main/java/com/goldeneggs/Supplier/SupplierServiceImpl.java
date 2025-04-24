package com.goldeneggs.Supplier;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of SupplierService interface.
 */
@Service
public class SupplierServiceImpl implements SupplierService {

    /**
     * Repository instance for managing supplier data storage and retrieval.
     * This variable is automatically injected by Spring to enable database operations
     * defined in the SupplierRepository interface.
     */
    @Autowired
    private SupplierRepository supplierRepository;

    /**
     * Retrieves all suppliers.
     *
     * @return A list of all suppliers.
     */
    @Override
    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    /**
     * Retrieves a supplier by its ID.
     *
     * @param id The ID of the supplier to retrieve.
     * @return The supplier with the specified ID.
     * @throws ResourceNotFoundException if no supplier is found with the specified ID.
     */
    @Override
    public Supplier get(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + id));
    }

    /**
     * Saves a new supplier or updates an existing one.
     *
     * @param supplier The supplier object to save.
     * @return The saved*/
    @Override
    public Supplier save(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    /**
     * Updates an existing supplier with the provided data.
     *
     * @param id The ID of the supplier to update.
     * @param updated The supplier data to update the existing supplier with.
     * @return The updated supplier.
     * @throws ResourceNotFoundException if no supplier is found with the specified ID.
     */
    @Override
    public Supplier update(Long id, Supplier updated) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + id));

        existing.setName(updated.getName());
        existing.setAddress(updated.getAddress());

        return supplierRepository.save(existing);
    }

    /**
     * Deletes a supplier by its ID.
     *
     * @param id The ID of the supplier to delete.
     * @throws ResourceNotFoundException if no supplier is found with the specified ID.
     */
    @Override
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id " + id);
        }
        supplierRepository.deleteById(id);
    }
}
