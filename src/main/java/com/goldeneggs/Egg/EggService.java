package com.goldeneggs.Egg;

import com.goldeneggs.Dto.Egg.EggSummaryDto;
import com.goldeneggs.Order.Order;
import com.goldeneggs.User.User;

import java.util.List;

/**
 * Service interface for managing eggs.
 */
public interface EggService {

    /**
     * Retrieves all eggs.
     *
     * @return A list of all eggs available in the system.
     */
    List<Egg> getAll();

    /**
     * Retrieves a specific egg by its ID.
     *
     * @param id The ID of the egg to retrieve.
     * @return The found egg, or null if not found.
     */
    Egg get(Long id);

    /**
     * Persists a new egg in the system.
     *
     * @param egg The egg entity to save.
     * @param idUser the id of the user make the save egg
     * @return The saved egg with an assigned ID.
     */
    Egg save(Egg egg, Long idUser);

    /**
     * Updates the data of an existing egg.
     *
     * @param id The ID of the egg to update.
     * @param updatedEgg The egg entity with updated values.
     * @return The updated egg, or null if the egg was not found.
     */
    Egg update(Long id, Egg updatedEgg,  Long idUser);

    /**
     * Deletes an egg from the system.
     *
     * @param id The ID of the egg to delete.
     */
    void delete(Long id);

    /**
     * Calculates the total quantity of eggs across all records in the system.
     *
     * @return The total number of eggs as a {@code Long}, or {@code null} if no records are present.
     */
    Long getTotalEggQuantity();

    /**
     * Retrives all tpye eggs with the max price and latest expiration date
     * @return A list of eggSummary
     */
    List<EggSummaryDto> findEggSummaries();

    /**
     * Update the egg quantity when new order is save
     * @param quantity the quantity requested
     */
    boolean updateEggQuantity(int quantity, String color, String type, User user, Order order);

    /**
     * Update the egg quantity when new order is canceled
     * @param totalEgg egg to restock
     * @param color color of the egg
     * @param type type egg
     * @param user User to control inventory
     * @param order the order to cancel
     * @return true if the quantity egg are restock
     */
    boolean restockEggs(int totalEgg,  String color, String type, User user, Order order);

}
