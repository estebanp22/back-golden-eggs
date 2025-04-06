package com.goldeneggs.Inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing inventory items.
 */
@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * Saves a new inventory item.
     *
     * @param inventory Inventory data.
     * @return Saved inventory item.
     */
    @PostMapping("/save")
    public ResponseEntity<Inventory> save(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.save(inventory));
    }

    /**
     * Retrieves an inventory item by ID.
     *
     * @param id Inventory ID.
     * @return Inventory item.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Inventory> get(@PathVariable Long id) {
        Inventory inv = inventoryService.get(id);
        if (inv == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(inv);
    }

    /**
     * Retrieves all inventory items.
     *
     * @return List of inventory items.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Inventory>> getAll() {
        return ResponseEntity.ok(inventoryService.getAll());
    }

    /**
     * Updates an existing inventory item.
     *
     * @param id Inventory ID.
     * @param inventory Updated inventory data.
     * @return Updated inventory item.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Inventory> update(@PathVariable Long id, @RequestBody Inventory inventory) {
        Inventory updated = inventoryService.update(id, inventory);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes an inventory item by ID.
     *
     * @param id Inventory ID.
     * @return HTTP 200 if deleted.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
