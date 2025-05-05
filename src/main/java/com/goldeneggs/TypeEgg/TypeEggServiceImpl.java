package com.goldeneggs.TypeEgg;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing egg types.
 */
@Service
public class TypeEggServiceImpl implements TypeEggService {

    @Autowired
    private TypeEggRepository typeEggRepository;

    /**
     * Saves or updates an egg type.
     *
     * @param typeEgg the type of egg to save
     * @return the saved TypeEgg
     */
    @Override
    public TypeEgg save(TypeEgg typeEgg) {
        return typeEggRepository.save(typeEgg);
    }

    /**
     * Retrieves all egg types.
     *
     * @return list of all TypeEgg entries
     */
    @Override
    public List<TypeEgg> getAll() {
        return typeEggRepository.findAll();
    }

    /**
     * Retrieves an egg type by ID.
     *
     * @param id the ID of the egg type
     * @return the corresponding TypeEgg
     * @throws ResourceNotFoundException if no egg type is found for the given ID
     */
    @Override
    public TypeEgg getById(Long id) {
        Optional<TypeEgg> optional = typeEggRepository.findById(id);
        return optional.orElseThrow(() -> new ResourceNotFoundException("Egg type not found with ID: " + id));
    }

    /**
     * Deletes an egg type by ID.
     *
     * @param id the ID of the egg type to delete
     */
    @Override
    public void delete(Long id) {
        typeEggRepository.deleteById(id);
    }
}
