package com.goldeneggs.Customer;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Interface for data access operations related to Customer.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}