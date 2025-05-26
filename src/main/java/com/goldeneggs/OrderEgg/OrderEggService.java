package com.goldeneggs.OrderEgg;

import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Exception.ResourceNotFoundException;

import java.util.List;

/**
 * Interface for order eggs service.
 * <p>
 * This interface defines the operations for managing order egg, including methods
 * for retrieving, saving, updating, and deleting order eggs in the system.
 * </p>
 */
public interface OrderEggService {
    /**
     * Retrieves all order eggs in the system.
     *
     * @return A list of all orderEggs.
     */
    List<OrderEgg> getAll();

    /**
     * Retrieves a order eggs by its ID.
     * <p>
     * If the order eggs with the given ID does not exist, it should return {@code null}
     * or throw a {@link ResourceNotFoundException} depending on the service design.
     * </p>
     *
     * @param id The ID of the order eggs to retrieve.
     * @return The order eggs associated with the given ID, or {@code null} if not found.
     * @throws ResourceNotFoundException If the order eggs with the given ID does not exist.
     */
    OrderEgg get(Long id);

    /**
     * Saves a new or existing order eggs.
     * <p>
     * If the order eggs already exists (based on ID or other criteria), it may update it;
     * otherwise, a new order eggs will be created.
     * </p>
     *
     * @param orderEgg The order eggs object to save or update.
     * @return The saved or updated order eggs.
     */
    OrderEgg save(OrderEgg orderEgg);

    /**
     * Updates an existing order eggs.
     * <p>
     * This method will update an existing order eggs's details based on the provided ID.
     * If no order eggs is found with the given ID, it may return {@code null} or throw an exception.
     * </p>
     *
     * @param id The ID of the order eggs to update.
     * @param updatedOrderEgg The new order eggs details.
     * @return The updated order eggs, or {@code null} if the order eggs does not exist.
     * @throws ResourceNotFoundException If the order eggs with the given ID does not exist.
     */
    OrderEgg update(Long id, OrderEgg updatedOrderEgg);

    /**
     * Deletes a order eggs by its ID.
     * <p>
     * This method will delete the order eggs from the system. If no order eggs is found with
     * the given ID, it may throw a {@link ResourceNotFoundException}.
     * </p>
     *
     * @param id The ID of the order eggs to delete.
     * @throws ResourceNotFoundException If the order eggs with the given ID does not exist.
     */
    void delete(Long id);

    OrderEgg createOrderEggForEgg(Egg egg);
}
