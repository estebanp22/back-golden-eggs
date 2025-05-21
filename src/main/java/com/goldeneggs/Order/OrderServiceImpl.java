package com.goldeneggs.Order;

import com.goldeneggs.Exception.InvalidOrderDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the OrderService interface.
 * <p>
 * This service provides the business logic for managing orders. It interacts with
 * the {@link OrderRepository} to perform CRUD operations on orders.
 * </p>
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    /**
     * Constructor to inject the {@link OrderRepository} dependency.
     *
     * @param orderRepository The order repository to interact with the database.
     */
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Retrieves all orders in the system.
     *
     * @return A list of all orders.
     */
    @Override
    public List<Order> getAllOrders() {return orderRepository.findAll();}

    /**
     * Finds an order by its ID.
     *
     * @param id The ID of the order to find.
     * @return The order if found, otherwise throws a {@link ResourceNotFoundException}.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order not found with ID: " + id)
        );
    }

    /**
     * Counts the total number of orders placed by a specific customer.
     *
     * @param customerId The ID of the customer whose orders are to be counted.
     * @return The total number of orders placed by the specified customer.
     */
    @Override
    public Long countOrdersByCustomerId(Long customerId) {
        return orderRepository.countOrdersByCustomerId(customerId);
    }


    /**
     * Saves a new or existing order.
     *
     * @param order The order to save.
     * @return The saved order. or error
     */
    @Override
    public Order saveOrder(Order order){
        validateOrderOrThrow(order);
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Long id, Order order) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " not found."));

        validateOrderOrThrow(existing);

        existing.setUser(order.getUser());
        existing.setOrderEggs(order.getOrderEggs());
        existing.setTotalPrice(order.getTotalPrice());
        existing.setOrderDate(order.getOrderDate());
        existing.setState(order.getState());
        return orderRepository.save(existing);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Order with ID " + id + " not found.")
        );
        orderRepository.delete(order);
    }

    /**
     * Retrieves a list of orders placed within the current month.
     *
     * @return A list of {@link Order} instances representing the orders placed
     *         between the start and end dates of the current month.
     */
    @Override
    public List<Order> getOrdersInCurrentMonth() {
        LocalDate now = LocalDate.now();
        Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return orderRepository.findOrdersInCurrentMonth(start, end);
    }

    /**
     * Counts the number of orders placed within the current month.
     * This method calculates the start and end dates of the current month
     * and queries the order repository to determine the total count of orders
     * placed during this period.
     *
     * @return The total count of orders placed in the current month. Returns 0 if no orders are found.
     */
    @Override
    public Long countOrdersInCurrentMonth() {
        LocalDate now = LocalDate.now();
        Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Long count = orderRepository.countOrdersInCurrentMonth(start, end);
        return count != null ? count : 0L;
    }

    private void validateOrderOrThrow(Order order) {
        if(!OrderValidator.validateUser(order.getUser())){
            throw new InvalidOrderDataException("User is not valid.");
        }
        if(!OrderValidator.validateOrderEggs(order.getOrderEggs())){
            throw new InvalidOrderDataException("Order Eggs are not valid.");
        }
        if(!OrderValidator.validateTotalPrice(order.getTotalPrice())){
            throw new InvalidOrderDataException("Total price is not valid.");
        }
        if(!OrderValidator.validateOrderDate(order.getOrderDate())){
            throw new InvalidOrderDataException("Order date is not valid.");
        }
        if(!OrderValidator.validateState(order.getState())){
            throw new InvalidOrderDataException("Order state is not valid.");
        }
    }
}
