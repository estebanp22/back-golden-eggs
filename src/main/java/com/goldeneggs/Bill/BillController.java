package com.goldeneggs.Bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing bills.
 */
@RestController
@RequestMapping("/api/v1/bill")
public class BillController {

    @Autowired
    private BillService billService;

    /**
     * Saves a new bill.
     *
     * @param bill The bill to save.
     * @return The saved bill.
     */
    @PostMapping("/save")
    public ResponseEntity<Bill> save(@RequestBody Bill bill) {
        return ResponseEntity.ok(billService.save(bill));
    }

    /**
     * Gets a bill by its ID.
     *
     * @param id Bill ID.
     * @return The bill if found.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Bill> get(@PathVariable Long id) {
        Bill bill = billService.get(id);
        if (bill == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(bill);
    }

    /**
     * Gets all bills.
     *
     * @return List of all bills.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Bill>> getAll() {
        return ResponseEntity.ok(billService.getAll());
    }

    /**
     * Updates a bill.
     *
     * @param id ID of the bill to update.
     * @param bill Updated bill data.
     * @return Updated bill if found.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Bill> update(@PathVariable Long id, @RequestBody Bill bill) {
        Bill updated = billService.update(id, bill);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a bill.
     *
     * @param id ID of the bill to delete.
     * @return HTTP 200 if deleted.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        billService.delete(id);
        return ResponseEntity.ok().build();
    }
}

