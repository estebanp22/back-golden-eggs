package com.goldeneggs.Order;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;

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
     * @throws ServiceException If an error occurs while fetching the orders from the repository.
     */
    @Override
    public List<Order> getAllOrders() {
        try {
            return orderRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Error retrieving orders.", e);
        }
    }

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
                new ResourceNotFoundException("Order with ID " + id + " not found.")
        );
    }

    /**
     * Saves a new or existing order.
     *
     * @param order The order to save.
     * @return The saved order.
     * @throws BadRequestException If the provided order data is invalid or incomplete.
     */
    @Override
    public Order saveOrder(Order order) throws BadRequestException {
        if (order == null) {
            throw new BadRequestException("Order data cannot be null.");
        }
        return orderRepository.save(order);
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
}
