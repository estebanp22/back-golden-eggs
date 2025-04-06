package com.goldeneggs.Supplier;

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
        return ResponseEntity.ok(supplierService.save(supplier));
    }

    /**
     * Gets a supplier by ID.
     *
     * @param id Supplier ID.
     * @return Supplier object.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Supplier> get(@PathVariable Long id) {
        Supplier supplier = supplierService.get(id);
        if (supplier == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(supplier);
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
        Supplier supplier = supplierService.update(id, updated);
        if (supplier == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(supplier);
    }

    /**
     * Deletes a supplier by ID.
     *
     * @param id Supplier ID.
     * @return HTTP 200 if deleted.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ResponseEntity.ok().build();
    }
}

