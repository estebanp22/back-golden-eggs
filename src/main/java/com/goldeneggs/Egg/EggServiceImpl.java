package com.goldeneggs.Egg;

import com.goldeneggs.Dto.Egg.EggSummaryDto;
import com.goldeneggs.Exception.InvalidEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.InventoryMovement.InventoryMovement;
import com.goldeneggs.InventoryMovement.InventoryMovementRepository;
import com.goldeneggs.Order.Order;
import com.goldeneggs.OrderEgg.OrderEggRepository;
import com.goldeneggs.Supplier.SupplierRepository;
import com.goldeneggs.TypeEgg.TypeEggRepository;
import com.goldeneggs.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing eggs.
 */
@Service
public class EggServiceImpl implements EggService {

    @Autowired
    private EggRepository eggRepository;

    @Autowired
    private TypeEggRepository typeEggRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private OrderEggRepository orderEggRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

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
     * @param idUser the user save the eggs
     * @return The saved egg entity.
     */
    @Override
    public Egg save(Egg egg, Long idUser) {
        validateEggOrThrow(egg);
        Egg savedEgg = eggRepository.save(egg);

        InventoryMovement movement = InventoryMovement.builder()
                .movementDate(new java.sql.Date(System.currentTimeMillis()))
                .combs(egg.getAvibleQuantity()/30)
                .egg(egg)
                .order(null)
                .user(userRepository.getById(idUser))
                .build();

        inventoryMovementRepository.save(movement);
        return savedEgg;
    }

    /**
     * Updates an existing egg with new data.
     *
     * @param id The ID of the egg to update.
     * @param updatedEgg The new data to update the egg with.
     * @return The updated egg if found, otherwise null.
     */
    @Override
    public Egg update(Long id, Egg updatedEgg, Long idUser) {
        Egg existing = eggRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Egg with ID " + id + " not found"));

        validateEggOrThrow(updatedEgg);
        InventoryMovement lastMovement = inventoryMovementRepository.findTopByEggOrderByMovementDateDesc(existing)
                .orElseThrow(() -> new ResourceNotFoundException("No inventory movement found for egg"));

        int newCombs = updatedEgg.getAvibleQuantity() / 30;
        existing.setType(updatedEgg.getType());
        existing.setColor(updatedEgg.getColor());
        existing.setExpirationDate(updatedEgg.getExpirationDate());
        existing.setBuyPrice(updatedEgg.getBuyPrice());
        existing.setSalePrice(updatedEgg.getSalePrice());
        existing.setAvibleQuantity(updatedEgg.getAvibleQuantity());

        Egg updated = eggRepository.save(existing);

        lastMovement.setCombs(newCombs);
        lastMovement.setMovementDate(new java.sql.Date(System.currentTimeMillis()));
        inventoryMovementRepository.save(lastMovement);

        return updated;
    }


    /**
     * Deletes an egg by its ID.
     *
     * @param id The ID of the egg to delete.
     */
    @Override
    public void delete(Long id) {
        if (!eggRepository.existsById(id)) {
            throw new ResourceNotFoundException("Egg with ID " + id + " not found");
        }

        Egg egg = eggRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Egg with ID " + id + " not found"));
        if (orderEggRepository.existsByTypeAndColorInActiveOrders(egg.getType().getType(), egg.getColor(), Order.STATE_PENDING)) {
            throw new InvalidEggDataException("Cannot delete egg with ID " + id + " because it is associated with an order");
        }

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

    @Override
    public List<EggSummaryDto> findEggSummaries() {
        return eggRepository.findEggSummaries();
    }

    private void validateEggOrThrow(Egg egg) {
        if (!EggValidator.validateTypeEgg(egg.getType())) {
            throw new InvalidEggDataException("Type egg not valid");
        }
        if(!typeEggRepository.existsById(egg.getType().getId())){
            throw new InvalidEggDataException("Type egg does not exist");
        }
        if (!EggValidator.validateColor(egg.getColor())) {
            throw new InvalidEggDataException("Color not valid");
        }
        if (!EggValidator.validateBuyPrice(egg.getBuyPrice())) {
            throw new InvalidEggDataException("buy price invalid");
        }
        if (!EggValidator.validateSalePrice(egg.getBuyPrice(), egg.getSalePrice())) {
            throw new InvalidEggDataException("sale price invalid");
        }
        if (!EggValidator.validateExpirationDate(egg.getExpirationDate())) {
            throw new InvalidEggDataException("The expiration date must be in the future.");
        }
        if (!EggValidator.validateSupplier(egg.getSupplier())) {
            throw new InvalidEggDataException("Supplier invalid");
        }
        if(!supplierRepository.existsById(egg.getSupplier().getId())){
            throw new InvalidEggDataException("Supplier does not exist");
        }
        if (!EggValidator.validateAviableQuantity(egg.getAvibleQuantity())) {
            throw new InvalidEggDataException("Aviable quantity invalid");
        }
    }
}
