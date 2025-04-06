package com.goldeneggs.Pay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing payments.
 */
@RestController
@RequestMapping("/api/v1/pay")
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
     * Gets a payment by its ID.
     *
     * @param id Payment ID.
     * @return Payment if found.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Pay> get(@PathVariable Long id) {
        Pay pay = payService.get(id);
        if (pay == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(pay);
    }

    /**
     * Gets all payments.
     *
     * @return List of all payments.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Pay>> getAll() {
        return ResponseEntity.ok(payService.getAll());
    }

    /**
     * Updates a payment.
     *
     * @param id ID of the payment to update.
     * @param pay Updated data.
     * @return Updated payment.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Pay> update(@PathVariable Long id, @RequestBody Pay pay) {
        Pay updated = payService.update(id, pay);
        if (updated == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a payment.
     *
     * @param id Payment ID.
     * @return HTTP 200 if deleted.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        payService.delete(id);
        return ResponseEntity.ok().build();
    }
}
