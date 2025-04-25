package com.goldeneggs.Pay;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing payments.
 */
@RestController
@RequestMapping("/api/v1/pay")
@CrossOrigin("*")
public class PayController {

    @Autowired
    private PayService payService;

    /**
     * Saves a new payment.
     *
     * @param pay Payment data.
     * @return Saved payment.
     */
    @PostMapping("/save")
    public ResponseEntity<Pay> save(@RequestBody Pay pay) {
        return ResponseEntity.ok(payService.save(pay));
    }

    /**
     * Retrieves a payment by its ID.
     *
     * @param id Payment ID.
     * @return Payment if found, or 404 Not Found.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Pay> get(@PathVariable Long id) {
        try {
            Pay pay = payService.get(id);
            return ResponseEntity.ok(pay);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all payments.
     *
     * @return List of all payments.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Pay>> getAll() {
        return ResponseEntity.ok(payService.getAll());
    }

    /**
     * Updates an existing payment.
     *
     * @param id  ID of the payment to update.
     * @param pay Updated payment data.
     * @return Updated payment if found, or 404 Not Found.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Pay> update(@PathVariable Long id, @RequestBody Pay pay) {
        try {
            Pay updated = payService.update(id, pay);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a payment by its ID.
     *
     * @param id Payment ID.
     * @return HTTP 200 OK if deleted, or 404 Not Found.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            payService.delete(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
