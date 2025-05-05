package com.goldeneggs.Egg;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for managing eggs.
 */
@Service
public class EggServiceImpl implements EggService {

    @Autowired
    private EggRepository eggRepository;

    /**
     * Retrieves all eggs from the database.
     *
     * @return List of all eggs.
     */
    @Override
    public List<Egg> getAll() {
        return eggRepository.findAll();
    }

    /**
     * Retrieves an egg by its ID.
     *
     * @param id The ID of the egg to retrieve.
     * @return The egg if found, otherwise null.
     */
    @Override
    public Egg get(Long id) {
        return eggRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Egg with ID " + id + " not found"));
    }

    /**
     * Saves a new egg to the database.
     *
     * @param egg The egg to save.
     * @return The saved egg entity.
     */
    @Override
    public Egg save(Egg egg) {
        return eggRepository.save(egg);
    }

    /**
     * Updates an existing egg with new data.
     *
     * @param id The ID of the egg to update.
     * @param updatedEgg The new data to update the egg with.
     * @return The updated egg if found, otherwise null.
     */
    @Override
    public Egg update(Long id, Egg updatedEgg) {
        Egg existing = eggRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Egg with ID " + id + " not found"));

        existing.setType(updatedEgg.getType());
        existing.setColor(updatedEgg.getColor());
        existing.setExpirationDate(updatedEgg.getExpirationDate());
        existing.setBuyPrice(updatedEgg.getBuyPrice());
        existing.setSalePrice(updatedEgg.getSalePrice());

        return eggRepository.save(existing);
    }


    /**
     * Deletes an egg by its ID.
     *
     * @param id The ID of the egg to delete.
     */
    @Override
    public void delete(Long id) {
        eggRepository.deleteById(id);
    }

    /**
     * Retrieves the total quantity of eggs across all records.
     *
     * @return The total quantity of eggs as a {@code Long}, or {@code null} if no records are present.
     */
    @Override
    public Long getTotalEggQuantity() {
        System.out.println(eggRepository.getTotalEggQuantity());
        Long total = eggRepository.getTotalEggQuantity();
        return total != null ? total : 0;
    }

}
