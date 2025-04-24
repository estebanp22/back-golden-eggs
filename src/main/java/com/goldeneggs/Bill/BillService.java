package com.goldeneggs.Bill;

import java.util.List;

/**
 * Service interface for handling operations related to {@link Bill}.
 * Provides methods to retrieve, create, update, and delete bills.
 *
 * Exception handling should be implemented in the service implementation,
 * using custom exceptions such as {@code ResourceNotFoundException}.
 */
public interface BillService {

    /**
     * Retrieves all bills.
     *
     * @return a list of all bills.
     */
    List<Bill> getAll();

    /**
     * Retrieves a bill by its unique ID.
     *
     * @param id the ID of the bill to retrieve.
     * @return the {@link Bill} with the specified ID.
     * @throws com.goldeneggs.Exception.ResourceNotFoundException if no bill is found with the given ID.
     */
    Bill get(Long id);

    /**
     * Saves a new bill to the system.
     *
     * @param bill the bill to be saved.
     * @return the saved {@link Bill}.
     */
    Bill save(Bill bill);

    /**
     * Updates an existing bill.
     *
     * @param id the ID of the bill to update.
     * @param updatedBill the updated bill information.
     * @return the updated {@link Bill}.
     * @throws com.goldeneggs.Exception.ResourceNotFoundException if no bill is found with the given ID.
     */
    Bill update(Long id, Bill updatedBill);

    /**
     * Deletes a bill by its ID.
     *
     * @param id the ID of the bill to delete.
     * @throws com.goldeneggs.Exception.ResourceNotFoundException if no bill is found with the given ID.
     */
    void delete(Long id);
}
