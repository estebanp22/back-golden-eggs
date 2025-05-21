package com.goldeneggs.Pay;

import com.goldeneggs.Exception.InvalidPayDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing payments.
 */
@RestController
@RequestMapping("/api/v1/payments")
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
    public ResponseEntity<?> save(@RequestBody Pay pay) {
        try {
         Pay saved = payService.save(pay);
         return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }catch (InvalidPayDataException e){
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Pay pay) {
        try {
            Pay updated = payService.update(id, pay);
            return ResponseEntity.ok(updated);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (InvalidPayDataException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
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

    /**
     * Returns the total income from all payments in the system.
     *
     * @return Total income as {@code Double}, or 0.0 if no payments exist.
     */
    @GetMapping("/totalIncome")
    public ResponseEntity<Double> getTotalIncome() {
        Double total = payService.totalIncome();
        return ResponseEntity.ok(total);
    }

    /**
     * Returns the total income from payments made during the current month.
     *
     * @return Total income for the current month as {@code Double}, or 0.0 if no payments exist.
     */
    @GetMapping("/totalIncomeCurrentMonth")
    public ResponseEntity<Double> getTotalIncomeThisMonth() {
        Double total = payService.totalIncomeCurrentMonth();
        return ResponseEntity.ok(total);
    }
}
