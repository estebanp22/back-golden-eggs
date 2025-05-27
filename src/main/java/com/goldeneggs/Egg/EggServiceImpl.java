package com.goldeneggs.Egg;

import com.goldeneggs.Bill.Bill;
import com.goldeneggs.Bill.BillService;
import com.goldeneggs.Dto.Egg.EggSummaryDto;
import com.goldeneggs.Exception.InvalidEggDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import com.goldeneggs.InventoryMovement.InventoryMovement;
import com.goldeneggs.InventoryMovement.InventoryMovementRepository;
import com.goldeneggs.InventoryMovement.InventoryMovementService;
import com.goldeneggs.Order.Order;
import com.goldeneggs.Order.OrderService;
import com.goldeneggs.OrderEgg.OrderEgg;
import com.goldeneggs.OrderEgg.OrderEggRepository;
import com.goldeneggs.OrderEgg.OrderEggService;
import com.goldeneggs.Pay.PayService;
import com.goldeneggs.Supplier.SupplierRepository;
import com.goldeneggs.TypeEgg.TypeEgg;
import com.goldeneggs.TypeEgg.TypeEggRepository;
import com.goldeneggs.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private InventoryMovementService inventoryMovementService;

    @Autowired
    private OrderEggService orderEggService;

    @Lazy
    @Autowired
    private OrderService orderService;

    @Autowired
    private BillService billService;

    @Autowired
    private PayService payService;

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

        List<OrderEgg> orderEggs = new ArrayList<>();
        OrderEgg orderEgg = orderEggService.createOrderEggForEgg(egg);
        orderEggs.add(orderEgg);

        Order order = orderService.createOrderForEgg(idUser, orderEggs, egg);
        orderEgg.setOrder(order);

        inventoryMovementService.createMovementForEgg(egg, order, idUser);

        Bill bill = billService.createBillForOrder(order);
        payService.createPayForBill(bill, "TRANSFERENCIA");

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

        // Actualiza campos del huevo
        existing.setType(updatedEgg.getType());
        existing.setColor(updatedEgg.getColor());
        existing.setExpirationDate(updatedEgg.getExpirationDate());
        existing.setBuyPrice(updatedEgg.getBuyPrice());
        existing.setSalePrice(updatedEgg.getSalePrice());
        existing.setAvibleQuantity(updatedEgg.getAvibleQuantity());

        Egg updated = eggRepository.save(existing);

        OrderEgg orderEgg = orderEggService.createOrderEggForEgg(updated);
        List<OrderEgg> orderEggs = List.of(orderEgg);

        Order order = orderService.createOrderForEgg(idUser, orderEggs, updated);
        orderEgg.setOrder(order);
        inventoryMovementService.createMovementForEgg(updated, order, idUser);

        Bill bill = billService.createBillForOrder(order);
        payService.createPayForBill(bill, "TRANSFERENCIA");

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

    /**
     * {@inheritDoc}
     * @param quantity the quantity requested
     * @param color color of the egg (Blanco, Rojo)
     * @param type type of the egg (AAA, AA , A, B)
     * @return true if the update the quantity correct, false if teh quantity is invalid
     */
    @Override
    public boolean updateEggQuantity(int quantity, String color, String type, User user, Order order) {
        if (quantity <= 0) {
            return false;
        }
        TypeEgg typeEgg = typeEggRepository.findByType(type);

        List<Egg> matchingEggs = eggRepository.findEggsByColorAndType(color, typeEgg);
        if (matchingEggs.isEmpty()) {
            return false;
        }

        matchingEggs.sort(Comparator.comparing(Egg::getExpirationDate));

        int remainingQuantity = quantity;
        List<Egg> affectedEggs = new ArrayList<>();
        Map<Egg, Integer> originalQuantities = new HashMap<>();
        List<InventoryMovement> movementsToSave = new ArrayList<>();

        for (Egg egg : matchingEggs) {
            if (remainingQuantity <= 0) {
                break;
            }

            int available = egg.getAvibleQuantity();
            if (available <= 0) continue;

            originalQuantities.put(egg, available);

            int toDeduct = Math.min(available, remainingQuantity);
            egg.setAvibleQuantity(available - toDeduct);
            affectedEggs.add(egg);
            remainingQuantity -= toDeduct;

            // Crear movimiento
            int combs = toDeduct / 30; // suponiendo que 1 comb = 30 huevos
            if (combs > 0) {
                InventoryMovement movement = InventoryMovement.builder()
                        .movementDate(new Date())
                        .combs(combs)
                        .egg(egg)
                        .user(user)
                        .order(order)
                        .build();
                movementsToSave.add(movement);
            }
        }

        if (remainingQuantity > 0) {
            for (Egg egg : affectedEggs) {
                Integer original = originalQuantities.get(egg);
                if (original != null) {
                    egg.setAvibleQuantity(original);
                }
            }
            return false;
        }

        eggRepository.saveAll(affectedEggs);
        inventoryMovementRepository.saveAll(movementsToSave);

        return true;
    }

    /**
     *
     * @param totalEgg egg to restock
     * @param color color of the egg
     * @param type type egg
     * @param user User to control inventory
     * @param order the order to cancel
     * @return
     */
    @Override
    public boolean restockEggs(int totalEgg,  String color, String type, User user, Order order){
        if (totalEgg <= 0) {
            return false;
        }

        TypeEgg typeEgg = typeEggRepository.findByType(type);
        if (typeEgg == null) {
            return false;
        }

        List<Egg> matchingEggs = eggRepository.findEggsByColorAndType(color, typeEgg);
        if (matchingEggs.isEmpty()) {
            // Si no hay huevos existentes, crea uno nuevo
            Egg newEgg = createNewEgg(color, typeEgg, totalEgg);
            eggRepository.save(newEgg);

            // Registra movimiento de reposición
            createInventoryMovement(newEgg, user, order, totalEgg, true);
            return true;
        }

        // Ordenar por fecha de expiración (los más antiguos primero)
        matchingEggs.sort(Comparator.comparing(Egg::getExpirationDate));

        int remainingQuantity = totalEgg;
        List<InventoryMovement> movementsToSave = new ArrayList<>();

        for (Egg egg : matchingEggs) {
            if (remainingQuantity <= 0) {
                break;
            }

            int currentQuantity = egg.getAvibleQuantity();
            int newQuantity = currentQuantity + remainingQuantity;
            egg.setAvibleQuantity(newQuantity);

            // Registrar movimiento (1 comb = 30 huevos)
            int combs = remainingQuantity / 30;
            if (combs > 0) {
                InventoryMovement movement = InventoryMovement.builder()
                        .movementDate(new Date())
                        .combs(combs)
                        .egg(egg)
                        .user(user)
                        .order(order)
                        .build();
                movementsToSave.add(movement);
            }

            remainingQuantity = 0;
        }

        // Si aún queda cantidad por reponer, crear nuevo lote
        if (remainingQuantity > 0) {
            Egg newEgg = createNewEgg(color, typeEgg, remainingQuantity);
            eggRepository.save(newEgg);
            createInventoryMovement(newEgg, user, order, remainingQuantity, true);
        }

        eggRepository.saveAll(matchingEggs);
        inventoryMovementRepository.saveAll(movementsToSave);

        return true;
    }

    public Egg createNewEgg(String color, TypeEgg typeEgg, int quantity) {
        Calendar calendario = new GregorianCalendar();

        // Agregar 15 días
        calendario.add(Calendar.DAY_OF_MONTH, 15);

        // Crear un objeto java.sql.Date
        java.sql.Date fechaFutura = new java.sql.Date(calendario.getTimeInMillis());
        return Egg.builder()
                .color(color)
                .type(typeEgg)
                .avibleQuantity(quantity)
                .expirationDate(fechaFutura) // Implementa tu lógica de fecha
                .build();
    }

    public void createInventoryMovement(Egg egg, User user, Order order, int quantity, boolean isRestock) {
        int combs = quantity / 30;
        if (combs > 0) {
            InventoryMovement movement = InventoryMovement.builder()
                    .movementDate(new Date())
                    .combs(combs)
                    .egg(egg)
                    .user(user)
                    .order(isRestock ? order : null)
                    .build();
            inventoryMovementRepository.save(movement);
        }
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
