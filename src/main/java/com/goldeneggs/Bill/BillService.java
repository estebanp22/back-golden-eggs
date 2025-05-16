package com.goldeneggs.Bill;

import com.goldeneggs.Dto.BillDto;

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
    List<BillDto> getAll();


    /**
     *returns all invoices sent to the company
     *
     * @return a list of all the company's invoices.
     */
    List<BillDto> getAllBillsForCompany();

    /**
     * returns all invoices sent to a client
     *
     * @return a list of all customer invoices.
     */
    List<BillDto> getAllBillsForCustomers();

    /**
     * Calculates the total sales amount for the current month.
     * Only bills associated with users who have the "CUSTOMER" role are considered.
     *
     * @return the total monthly sales as a {@link Double}.
     */
    Double getMonthlySalesTotal();

    /**
     * Determines the customer who spent the most during the current month.
     * The evaluation is based only on users with the "CUSTOMER" role.
     *
     * @return the name of the top-spending customer for the current month,
     *         or "Sin compras este mes" if no qualifying purchases exist.
     */
    String getBestCustomerOfMonth();

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
     * Retrieves all bills associated with a specific customer.
     *
     * @param customerId the unique identifier of the customer whose bills are to be retrieved.
     * @return a list of {@link BillDto} objects representing the bills of the specified customer.
     */
    List<BillDto> getBillsByCustomer(Long customerId);


    /**
     * Deletes a bill by its ID.
     *
     * @param id the ID of the bill to delete.
     * @throws com.goldeneggs.Exception.ResourceNotFoundException if no bill is found with the given ID.
     */
    void delete(Long id);

    /**
     * Retrieves the number of bills issued in the current month.
     *
     * @return The total number of bills issued in the current month. Returns 0 if no bills are found.
     */
    Long countCustomerBillsInCurrentMonth();
}
