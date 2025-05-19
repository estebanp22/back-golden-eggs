package com.goldeneggs.InventoryMovement;

import com.goldeneggs.Exception.InvalidInventoryMovementDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing inventory items.
 */
@RestController
@RequestMapping("/api/v1/inventories")
@CrossOrigin("*")
public class InventoryMovementController {

    @Autowired
    private InventoryMovementService inventoryMovementService;

    /**
     * Saves a new inventory item.
     *
     * @param inventoryMovement Inventory data to be saved.
     * @return ResponseEntity containing the saved inventory item.
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody InventoryMovement inventoryMovement) {
        try{
            InventoryMovement saved = inventoryMovementService.save(inventoryMovement);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }catch (InvalidInventoryMovementDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves an inventory item by its ID.
     *
     * @param id The ID of the inventory item to retrieve.
     * @return ResponseEntity containing the inventory item if found, or 404 Not Found if not.
     * @throws ResourceNotFoundException If the inventory item with the given ID does not exist.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<InventoryMovement> get(@PathVariable Long id) {
        InventoryMovement inv = inventoryMovementService.get(id);
        if (inv == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(inv);
    }

    /**
     * Retrieves all inventory items.
     *
     * @return ResponseEntity containing a list of all inventory items.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<InventoryMovement>> getAll() {
        return ResponseEntity.ok(inventoryMovementService.getAll());
    }

    /**
     * Updates an existing inventory item.
     *
     * @param id The ID of the inventory item to update.
     * @param inventoryMovement The updated inventory data.
     * @return ResponseEntity containing the updated inventory item, or 404 Not Found if the item does not exist.
     * @throws ResourceNotFoundException If the inventory item with the given ID does not exist.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InventoryMovement inventoryMovement) {
        try{
            InventoryMovement updated = inventoryMovementService.update(id, inventoryMovement);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (InvalidInventoryMovementDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
        try {
            inventoryMovementService.delete(id);
            return ResponseEntity.ok().build();
        }catch (ResourceNotFoundException e){
            return ResponseEntity.noContent().build();
        }
    }
}
