package com.goldeneggs.Inventory;

import java.util.List;

/**
 * Service interface for inventory management.
 */
public interface InventoryService {
    List<Inventory> getAll();
    Inventory get(Long id);
    Inventory save(Inventory inventory);
    Inventory update(Long id, Inventory updatedInventory);
    void delete(Long id);
}
