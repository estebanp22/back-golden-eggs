package com.goldeneggs.Inventory;

import java.util.List;

/**
 * Service interface for managing inventory operations.
 * Provides standard CRUD functionalities for inventory entities.
 */
public interface InventoryService {

    /**
     * Retrieves all inventory items.
     *
     * @return a list of all inventory items.
     */
    List<Inventory> getAll();

    /**
     * Retrieves a specific inventory item by its ID.
     *
     * @param id the ID of the inventory item to retrieve.
     * @return the inventory item with the specified ID.
     */
    Inventory get(Long id);

    /**
     * Saves a new inventory item.
     *
     * @param inventory the inventory item to save.
     * @return the saved inventory item.
     */
    Inventory save(Inventory inventory);

    /**
     * Updates an existing inventory item.
     *
     * @param id the ID of the inventory item to update.
     * @param updatedInventory the inventory item with updated information.
     * @return the updated inventory item.
     */
    Inventory update(Long id, Inventory updatedInventory);

    /**
     * Deletes an inventory item by its ID.
     *
     * @param id the ID of the inventory item to delete.
     */
    void delete(Long id);
}
