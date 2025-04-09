package com.goldeneggs.Inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of InventoryService interface.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory get(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    @Override
    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory update(Long id, Inventory updatedInventory) {
        Inventory existing = get(id);
        if (existing == null) return null;

        existing.setNameProduct(updatedInventory.getNameProduct());
        existing.setAvailableQuantity(updatedInventory.getAvailableQuantity());
        existing.setPrice(updatedInventory.getPrice());
        existing.setEntryDate(updatedInventory.getEntryDate());

        return inventoryRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }
}
