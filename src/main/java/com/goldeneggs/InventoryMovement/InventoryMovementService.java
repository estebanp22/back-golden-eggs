package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Dto.InventoryMovement.InventoryMovementDTO;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Order.Order;
import com.goldeneggs.OrderEgg.OrderEgg;

import java.util.List;

/**
 * Service interface for managing inventory operations.
 * Provides standard CRUD functionalities for inventory entities.
 */
public interface InventoryMovementService {

    /**
     * Retrieves all inventory items.
     *
     * @return a list of all inventory items.
     */
    List<InventoryMovementDTO> getAll();

    /**
     * Retrieves a specific inventory item by its ID.
     *
     * @param id the ID of the inventory item to retrieve.
     * @return the inventory item with the specified ID.
     */
    InventoryMovement get(Long id);

    /**
     * Saves a new inventory item.
     *
     * @param inventoryMovement the inventory item to save.
     * @return the saved inventory item.
     */
    InventoryMovement save(InventoryMovement inventoryMovement);

    /**
     * Updates an existing inventory item.
     *
     * @param id                       the ID of the inventory item to update.
     * @param updatedInventoryMovement the inventory item with updated information.
     * @return the updated inventory item.
     */
    InventoryMovement update(Long id, InventoryMovement updatedInventoryMovement);

    /**
     * Deletes an inventory item by its ID.
     *
     * @param id the ID of the inventory item to delete.
     */
    void delete(Long id);

    /**
     * Create a movement inventory when add a egg
     * @param egg represent the egg of the inventory
     * @param order represent the order og the inventory
     * @param idUser represent the user to make the movement
     * @return InventoryMovement
     */
    void createMovementForEgg(Egg egg, Order order, Long idUser);
}
