package com.goldeneggs.Customer;

import java.util.List;

/**
 * Interface defining the operations related to customer management.
 */

public interface CustomerService {
    List<Customer> getAll();
    Customer get(Long id);
    Customer save(Customer customer);
    Customer update(Customer customer);
    void delete(Long id);
}