package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the InventoryService interface.
 * Handles business logic for inventory-related operations.
 */
@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<InventoryMovement> getAll() {
        return inventoryMovementRepository.findAll();
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the inventory item is not found.
     */
    @Override
    public InventoryMovement get(Long id) {
        return inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory with ID " + id + " not found."));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InventoryMovement save(InventoryMovement inventoryMovement) {
        return inventoryMovementRepository.save(inventoryMovement);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the inventory item to update is not found.
     */
    @Override
    public InventoryMovement update(Long id, InventoryMovement updatedInventoryMovement) {
        InventoryMovement existing = get(id); // Will throw exception if not foun

        return inventoryMovementRepository.save(existing);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the inventory item to delete is not found.
     */
    @Override
    public void delete(Long id) {
        InventoryMovement inventoryMovement = get(id); // Will throw exception if not found
        inventoryMovementRepository.deleteById(id);
    }
}
