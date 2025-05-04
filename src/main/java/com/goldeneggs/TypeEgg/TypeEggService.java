package com.goldeneggs.TypeEgg;

import java.util.List;

/**
 * Service interface for managing egg types.
 */
public interface TypeEggService {

    /**
     * Saves a new or existing egg type.
     *
     * @param typeEgg the type of egg to save
     * @return the saved TypeEgg
     */
    TypeEgg save(TypeEgg typeEgg);

    /**
     * Retrieves all egg types.
     *
     * @return list of all TypeEgg entries
     */
    List<TypeEgg> getAll();

    /**
     * Finds an egg type by its ID.
     *
     * @param id the ID of the egg type
     * @return the TypeEgg if found, otherwise null
     */
    TypeEgg getById(Long id);

    /**
     * Deletes an egg type by its ID.
     *
     * @param id the ID of the egg type to delete
     */
    void delete(Long id);
}
