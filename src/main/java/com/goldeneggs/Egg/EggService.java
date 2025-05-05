package com.goldeneggs.Egg;

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
     * @return The saved egg with an assigned ID.
     */
    Egg save(Egg egg);

    /**
     * Updates the data of an existing egg.
     *
     * @param id The ID of the egg to update.
     * @param updatedEgg The egg entity with updated values.
     * @return The updated egg, or null if the egg was not found.
     */
    Egg update(Long id, Egg updatedEgg);

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
    public Long getTotalEggQuantity();
}
