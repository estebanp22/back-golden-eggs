package com.goldeneggs.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing customers.
 */

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * Saves a new customer.
     * @param customer the customer to save
     * @return saved customer
     */
    @PostMapping("/save")
    public ResponseEntity<Customer> save(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.save(customer));
    }

    /**
     * Gets a customer by ID.
     * @param id the customer ID
     * @return the customer
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Customer> get(@PathVariable Long id) {
        Customer customer = customerService.get(id);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    /**
     * Gets all customers.
     * @return list of customers
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(customerService.getAll());
    }

    /**
     * Updates a customer.
     * @param customer the customer to update
     * @return updated customer
     */
    @PutMapping("/update")
    public ResponseEntity<Customer> update(@RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.update(customer));
    }

    /**
     * Deletes a customer by ID.
     * @param id the ID of the customer
     * @return status response
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
