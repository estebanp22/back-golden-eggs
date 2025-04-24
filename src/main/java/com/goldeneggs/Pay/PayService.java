package com.goldeneggs.Pay;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;

import java.util.List;

/**
 * Interface for payment service.
 * <p>
 * This interface defines the operations for managing payments, including methods
 * for retrieving, saving, updating, and deleting payments in the system.
 * </p>
 */
public interface PayService {

    /**
     * Retrieves all payments in the system.
     *
     * @return A list of all payments.
     */
    List<Pay> getAll();

    /**
     * Retrieves a payment by its ID.
     * <p>
     * If the payment with the given ID does not exist, it should return {@code null}
     * or throw a {@link ResourceNotFoundException} depending on the service design.
     * </p>
     *
     * @param id The ID of the payment to retrieve.
     * @return The payment associated with the given ID, or {@code null} if not found.
     * @throws ResourceNotFoundException If the payment with the given ID does not exist.
     */
    Pay get(Long id);

    /**
     * Saves a new or existing payment.
     * <p>
     * If the payment already exists (based on ID or other criteria), it may update it;
     * otherwise, a new payment will be created.
     * </p>
     *
     * @param pay The payment object to save or update.
     * @return The saved or updated payment.
     * @throws BadRequestException If the payment data is invalid or incomplete.
     */
    Pay save(Pay pay);

    /**
     * Updates an existing payment.
     * <p>
     * This method will update an existing payment's details based on the provided ID.
     * If no payment is found with the given ID, it may return {@code null} or throw an exception.
     * </p>
     *
     * @param id The ID of the payment to update.
     * @param updatedPay The new payment details.
     * @return The updated payment, or {@code null} if the payment does not exist.
     * @throws ResourceNotFoundException If the payment with the given ID does not exist.
     */
    Pay update(Long id, Pay updatedPay);

    /**
     * Deletes a payment by its ID.
     * <p>
     * This method will delete the payment from the system. If no payment is found with
     * the given ID, it may throw a {@link ResourceNotFoundException}.
     * </p>
     *
     * @param id The ID of the payment to delete.
     * @throws ResourceNotFoundException If the payment with the given ID does not exist.
     */
    void delete(Long id);
}
