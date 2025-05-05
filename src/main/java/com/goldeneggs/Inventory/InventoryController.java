package com.goldeneggs.Inventory;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing inventory items.
 */
@RestController
@RequestMapping("/api/v1/inventories")
@CrossOrigin("*")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * Saves a new inventory item.
     *
     * @param inventory Inventory data to be saved.
     * @return ResponseEntity containing the saved inventory item.
     */
    @PostMapping("/save")
    public ResponseEntity<Inventory> save(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.save(inventory));
    }

    /**
     * Retrieves an inventory item by its ID.
     *
     * @param id The ID of the inventory item to retrieve.
     * @return ResponseEntity containing the inventory item if found, or 404 Not Found if not.
     * @throws ResourceNotFoundException If the inventory item with the given ID does not exist.
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
     * @return ResponseEntity containing a list of all inventory items.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Inventory>> getAll() {
        return ResponseEntity.ok(inventoryService.getAll());
    }

    /**
     * Updates an existing inventory item.
     *
     * @param id The ID of the inventory item to update.
     * @param inventory The updated inventory data.
     * @return ResponseEntity containing the updated inventory item, or 404 Not Found if the item does not exist.
     * @throws ResourceNotFoundException If the inventory item with the given ID does not exist.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Inventory> update(@PathVariable Long id, @RequestBody Inventory inventory) {
        Inventory updated = inventoryService.update(id, inventory);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes an inventory item by its ID.
     *
     * @param id The ID of the inventory item to delete.
     * @return ResponseEntity with HTTP 200 if the item was successfully deleted, or 404 if not found.
     * @throws ResourceNotFoundException If the inventory item with the given ID does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
