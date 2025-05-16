package com.goldeneggs.Order;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.hibernate.service.spi.ServiceException;

import java.util.List;

/**
 * Service interface for managing Orders.
 */
public interface OrderService {

    /**
     * Retrieves all orders in the system.
     *
     * @return A list of all orders.
     * @throws ServiceException If an error occurs while fetching the orders.
     */
    List<Order> getAllOrders();

    /**
     * Finds an order by its ID.
     *
     * @param id The ID of the order to find.
     * @return The order if found, otherwise throws ResourceNotFoundException.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    Order getOrderById(Long id);

    /**
     * Saves a new or existing order.
     *
     * @param order The order to save.
     * @return The saved order.
     * @throws BadRequestException If the provided order data is invalid or incomplete.
     */
    Order saveOrder(Order order) throws BadRequestException;

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    void deleteOrder(Long id);

    /**
     * Retrieves a list of orders placed in the current month.
     *
     * @return A list of orders that were created within the current month.
     */
    List<Order> getOrdersInCurrentMonth();

    /**
     * Counts the total number of orders placed within the current month.
     *
     * @return The total count of orders created in the current month.
     */
    Long countOrdersInCurrentMonth();

    /**
     * Counts the total number of orders placed by a specific customer.
     *
     * @param customerId The ID of the customer whose orders are to be counted.
     * @return The total number of orders placed by the specified customer.
     */
    Long countOrdersByCustomerId(Long customerId);

}
