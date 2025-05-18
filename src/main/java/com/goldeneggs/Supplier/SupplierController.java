package com.goldeneggs.Supplier;

import com.goldeneggs.Exception.InvalidSupplierDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing suppliers.
 */
@RestController
@RequestMapping("/api/v1/suppliers")
@CrossOrigin("*")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * Saves a new supplier to the system.
     *
     * @param supplier The supplier object containing details to be saved.
     * @return A ResponseEntity containing the created supplier and HTTP status
     *         code 201 (Created) if successful, or an error message with the appropriate
     *         HTTP status code if an error occurs.
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Supplier supplier) {
        try {
            Supplier saved = supplierService.save(supplier);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (InvalidSupplierDataException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the details of an existing supplier.
     *
     * @param id The ID of the supplier to update.
     * @param updated The supplier object containing updated details.
     * @return A ResponseEntity containing the updated supplier object and HTTP status code 200 (OK)
     *         if the update is successful, or an error message with the appropriate HTTP status code
     *         if an error occurs, such as 404 (Not Found) for a missing supplier, 400 (Bad Request) for
     *         invalid data, or 500 (Internal Server Error) for unexpected issues.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Supplier updated) {
        try {
            Supplier result = supplierService.update(id, updated);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidSupplierDataException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Unexpected error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves a supplier by its ID.
     *
     * @param id The ID of the supplier to retrieve.
     * @return A ResponseEntity containing the supplier if found, or a response
     *         with HTTP status 404 (Not Found) if the supplier does not exist.
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
     * @return A ResponseEntity containing a list of all suppliers.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Supplier>> getAll() {
        return ResponseEntity.ok(supplierService.getAll());
    }

    /**
     * Deletes a supplier by its ID.
     *
     * @param id The ID of the supplier to delete.
     * @return A ResponseEntity with HTTP status 200 (OK) if the supplier was successfully deleted,
     *         or HTTP status 404 (Not Found) if the supplier does not exist.
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
