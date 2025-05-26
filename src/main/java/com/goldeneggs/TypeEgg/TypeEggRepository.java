package com.goldeneggs.TypeEgg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for type egg management.
 */
@Repository
public interface TypeEggRepository extends JpaRepository<TypeEgg, Long> {

    /**
     * Checks if a type of egg already exists (case insensitive).
     *
     * @param type the egg type
     * @return true if exists, false otherwise
     */
    boolean existsByTypeIgnoreCase(String type);

    /**
     * Checks whether a type of egg exists in a case-insensitive manner,
     * excluding the entry with the specified ID.
     *
     * @param type the type of egg to check
     * @param id the ID to exclude from the search
     * @return true if a matching type exists excluding the given ID, false otherwise
     */
    boolean existsByTypeIgnoreCaseAndIdNot(String type, Long id);

    TypeEgg findByType(String type);
}
