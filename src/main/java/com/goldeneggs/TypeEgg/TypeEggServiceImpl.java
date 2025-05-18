package com.goldeneggs.TypeEgg;

import com.goldeneggs.Exception.InvalidTypeEggDataException;
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
     * Saves a TypeEgg entity to the repository. Validates the input entity and ensures that the type
     * does not already exist (case insensitive). If the ID is provided, it checks that the specified
     * ID exists and validates type uniqueness across other records. If validation passes,
     * the TypeEgg entity is persisted to the repository.
     *
     * @param typeEgg the TypeEgg entity to save
     * @return the saved TypeEgg entity
     * @throws InvalidTypeEggDataException if the provided TypeEgg entity is invalid or its type already exists
     * @throws ResourceNotFoundException if the provided ID is not found in the repository
     */
    @Override
    public TypeEgg save(TypeEgg typeEgg) {
        if (!TypeEggValidator.isValid(typeEgg)) {
            throw new InvalidTypeEggDataException("Egg type is invalid.");
        }

        boolean alreadyExists = typeEggRepository.existsByTypeIgnoreCase(typeEgg.getType());

        if (alreadyExists) {
            throw new InvalidTypeEggDataException("Egg type already exists.");
        }

        if (typeEgg.getId() != null) {
            Optional<TypeEgg> existing = typeEggRepository.findById(typeEgg.getId());

            if (existing.isEmpty()) {
                throw new ResourceNotFoundException("TypeEgg not found with ID: " + typeEgg.getId());
            }

            // If type changed and the new one already exists in another record
            if (!existing.get().getType().equalsIgnoreCase(typeEgg.getType())) {
                throw new InvalidTypeEggDataException("Another egg type with the same name already exists.");
            }
        }

        return typeEggRepository.save(typeEgg);
    }

    /**
     * Updates an existing egg type.
     *
     * @param id the ID of the egg type to update
     * @param updated the new data for the egg type
     * @return the updated TypeEgg
     * @throws ResourceNotFoundException if no egg type is found with the given ID
     * @throws InvalidTypeEggDataException if the updated type already exists
     */
    @Override
    public TypeEgg update(Long id, TypeEgg updated) {
        TypeEgg existing = typeEggRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Egg type not found with ID: " + id));

        if (typeEggRepository.existsByTypeIgnoreCaseAndIdNot(updated.getType(), id)) {
            throw new InvalidTypeEggDataException("Egg type already exists: " + updated.getType());
        }

        existing.setType(updated.getType());
        return typeEggRepository.save(existing);
    }

    /**
     * Retrieves all available egg types from the repository.
     *
     * @return a list of TypeEgg entities representing all available egg types
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
        if (!typeEggRepository.existsById(id)) {
            throw new ResourceNotFoundException("Type Egg not found with id " + id);
        }
        typeEggRepository.deleteById(id);
    }
}
