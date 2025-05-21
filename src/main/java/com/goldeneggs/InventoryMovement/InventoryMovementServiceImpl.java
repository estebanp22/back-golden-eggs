package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Exception.InvalidInventoryMovementDataException;
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

    @Autowired
    private InventoryMovementValidator inventoryMovementValidator;

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
        validateInventoryMovementOrThrow(inventoryMovement);
        return inventoryMovementRepository.save(inventoryMovement);
    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the inventory item to update is not found.
     */
    @Override
    public InventoryMovement update(Long id, InventoryMovement updatedInventoryMovement) {
        InventoryMovement existing = inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory movement with ID " + id + " not found."));

        validateInventoryMovementOrThrow(updatedInventoryMovement);

        existing.setEgg(updatedInventoryMovement.getEgg());
        existing.setCombs(updatedInventoryMovement.getCombs());
        existing.setMovementDate(updatedInventoryMovement.getMovementDate());
        existing.setOrder(updatedInventoryMovement.getOrder());
        existing.setUser(updatedInventoryMovement.getUser());
        return inventoryMovementRepository.save(existing);

    }

    /**
     * {@inheritDoc}
     *
     * @throws ResourceNotFoundException if the inventory item to delete is not found.
     */
    @Override
    public void delete(Long id) {
        if(!inventoryMovementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventory movement with ID " + id + " not found.");
        }
        inventoryMovementRepository.deleteById(id);
    }

    private void validateInventoryMovementOrThrow(InventoryMovement movement) {
        if (!inventoryMovementValidator.validateEgg(movement.getEgg())) {
            throw new InvalidInventoryMovementDataException("Huevo no válido o no existente");
        }
        if (!inventoryMovementValidator.validateMovementDate(movement.getMovementDate())) {
            throw new InvalidInventoryMovementDataException("Fecha no válida");
        }
        if (!inventoryMovementValidator.validateCombs(movement.getCombs())) {
            throw new InvalidInventoryMovementDataException("Panales requeridos inválidos");
        }
        if (!inventoryMovementValidator.validateUser(movement.getUser())) {
            throw new InvalidInventoryMovementDataException("Usuario no válido");
        }
    }
}
