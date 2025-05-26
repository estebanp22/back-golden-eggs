package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Dto.InventoryMovement.InventoryMovementDTO;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Exception.InvalidInventoryMovementDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.Egg.EggRepository;
import com.goldeneggs.Order.Order;
import com.goldeneggs.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the InventoryService interface.
 * Handles business logic for inventory-related operations.
 */
@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private EggRepository eggRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<InventoryMovementDTO> getAll() {
        List<InventoryMovement> movements = inventoryMovementRepository.findAll();
        return movements.stream()
                .map(InventoryMovementDTO::new)
                .collect(Collectors.toList());
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

    @Override
    public void createMovementForEgg(Egg egg, Order order, Long id){

        InventoryMovement movement = new InventoryMovement();
        movement.setMovementDate(new java.sql.Date(System.currentTimeMillis()));
        movement.setCombs(egg.getAvibleQuantity()/30);
        movement.setEgg(egg);
        movement.setOrder(order);
        movement.setUser(userRepository.findById(id).get());

        inventoryMovementRepository.save(movement);
    }

    private void validateInventoryMovementOrThrow(InventoryMovement movement) {
        if (!InventoryMovementValidator.validateEgg(movement.getEgg())) {
            throw new InvalidInventoryMovementDataException("Invalid egg");
        }
        if(!eggRepository.existsById(movement.getEgg().getId())){
            throw new InvalidInventoryMovementDataException("Egg does not exist");
        }
        if (!InventoryMovementValidator.validateMovementDate(movement.getMovementDate())) {
            throw new InvalidInventoryMovementDataException("Invalid date");
        }
        if (!InventoryMovementValidator.validateCombs(movement.getCombs())) {
            throw new InvalidInventoryMovementDataException("Invalid combs");
        }
        if (!InventoryMovementValidator.validateUser(movement.getUser())) {
            throw new InvalidInventoryMovementDataException("Invalid user");
        }
        if(!userRepository.existsById(movement.getUser().getId())){
            throw new InvalidInventoryMovementDataException("User does not exist");
        }
    }
}
