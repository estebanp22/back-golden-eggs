package com.goldeneggs.Bill;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link Bill} resources.
 */
@RestController
@RequestMapping("/api/v1/bill")
@CrossOrigin("*")
public class BillController {

    @Autowired
    private BillService billService;

    /**
     * Creates and saves a new bill.
     *
     * @param bill The bill to be saved.
     * @return The saved {@link Bill} entity.
     */
    @PostMapping("/save")
    public ResponseEntity<Bill> save(@RequestBody Bill bill) {
        return ResponseEntity.ok(billService.save(bill));
    }

    /**
     * Retrieves a bill by its ID.
     *
     * @param id The ID of the bill.
     * @return The {@link Bill} with the given ID.
     * @throws ResourceNotFoundException if the bill is not found.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Bill> get(@PathVariable Long id) {
        return ResponseEntity.ok(billService.get(id));
    }

    /**
     * Retrieves all bills in the system.
     *
     * @return A list of all {@link Bill} entities.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok(billService.getAll());
    }

    /**
     * Updates an existing bill with the given ID.
     *
     * @param id   The ID of the bill to update.
     * @param bill The updated bill data.
     * @return The updated {@link Bill} entity.
     * @throws ResourceNotFoundException if the bill is not found.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Bill> update(@PathVariable Long id, @RequestBody Bill bill) {
        return ResponseEntity.ok(billService.update(id, bill));
    }

    /**
     * Deletes a bill by its ID.
     *
     * @param id The ID of the bill to delete.
     * @return HTTP 200 OK if successfully deleted.
     * @throws ResourceNotFoundException if the bill is not found.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        billService.delete(id);
        return ResponseEntity.ok().build();
    }
}
