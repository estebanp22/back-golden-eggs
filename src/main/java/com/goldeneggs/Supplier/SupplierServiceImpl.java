package com.goldeneggs.Supplier;

import com.goldeneggs.Exception.InvalidSupplierDataException;
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
     * Saves a new supplier after validating its data.
     *
     * @param supplier The supplier object to save.
     * @return The saved supplier.
     * @throws InvalidSupplierDataException if the supplier data is invalid or already exists
     */
    @Override
    public Supplier save(Supplier supplier) {
        if (!SupplierValidator.isValid(supplier)) {
            throw new InvalidSupplierDataException("Supplier data is invalid.");
        }

        if (supplierRepository.existsByNameIgnoreCaseAndAddressIgnoreCase(supplier.getName(), supplier.getAddress())) {
            throw new InvalidSupplierDataException("A supplier with the same name and address already exists.");
        }

        return supplierRepository.save(supplier);
    }

    /**
     * Updates an existing supplier's information identified by the given ID.
     *
     * @param id The ID of the supplier to update.
     * @param updated The supplier object containing updated information.
     * @return The updated supplier object after saving to the database.
     * @throws ResourceNotFoundException if no supplier is found with the specified ID.
     * @throws InvalidSupplierDataException if the updated supplier data is invalid or conflicts with another existing supplier.
     */
    @Override
    public Supplier update(Long id, Supplier updated) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id " + id));

        if (!SupplierValidator.isValid(updated)) {
            throw new InvalidSupplierDataException("Supplier data is invalid.");
        }

        boolean duplicate = supplierRepository.existsByNameIgnoreCaseAndAddressIgnoreCase(updated.getName(), updated.getAddress())
                && !(existing.getName().equalsIgnoreCase(updated.getName()) &&
                existing.getAddress().equalsIgnoreCase(updated.getAddress()));

        if (duplicate) {
            throw new InvalidSupplierDataException("Another supplier with the same name and address already exists.");
        }

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
