package com.goldeneggs.Inventory;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the InventoryService interface.
 * Handles business logic for inventory-related operations.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the inventory item is not found.
     */
    @Override
    public Inventory get(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with ID " + id + " not found."));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the inventory item to update is not found.
     */
    @Override
    public Inventory update(Long id, Inventory updatedInventory) {
        Inventory existing = get(id); // Will throw exception if not found

        existing.setNameProduct(updatedInventory.getNameProduct());
        existing.setAvailableQuantity(updatedInventory.getAvailableQuantity());
        existing.setEntryDate(updatedInventory.getEntryDate());

        return inventoryRepository.save(existing);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the inventory item to delete is not found.
     */
    @Override
    public void delete(Long id) {
        Inventory inventory = get(id); // Will throw exception if not found
        inventoryRepository.deleteById(id);
    }
}
