package com.goldeneggs.Supplier;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing suppliers.
 */
@RestController
@RequestMapping("/api/v1/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * Saves a new supplier.
     *
     * @param supplier Supplier data.
     * @return Saved supplier.
     */
    @PostMapping("/save")
    public ResponseEntity<Supplier> save(@RequestBody Supplier supplier) {
        try {
            return ResponseEntity.ok(supplierService.save(supplier));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Gets a supplier by ID.
     *
     * @param id Supplier ID.
     * @return Supplier object.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Supplier> get(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(supplierService.get(id));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all suppliers.
     *
     * @return List of suppliers.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Supplier>> getAll() {
        return ResponseEntity.ok(supplierService.getAll());
    }

    /**
     * Updates a supplier by ID.
     *
     * @param id Supplier ID.
     * @param updated Updated supplier data.
     * @return Updated supplier.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Supplier> update(@PathVariable Long id, @RequestBody Supplier updated) {
        try {
            return ResponseEntity.ok(supplierService.update(id, updated));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a supplier by ID.
     *
     * @param id Supplier ID.
     * @return HTTP 200 if deleted.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            supplierService.delete(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
