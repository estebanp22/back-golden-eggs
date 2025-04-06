package com.goldeneggs.Order;

import java.util.List;

/**
 * Service interface for managing Orders.
 */
public interface OrderService {

    /**
     * Returns all orders in the system.
     * @return list of all orders
     */
    List<Order> getAllOrders();

    /**
     * Finds an order by its ID.
     * @param id the ID of the order
     * @return the order if found, otherwise null
     */
    Order getOrderById(Long id);

    /**
     * Saves a new or existing order.
     * @param order the order to save
     * @return the saved order
     */
    Order saveOrder(Order order);

    /**
     * Deletes an order by ID.
     * @param id the ID of the order to delete
     */
    void deleteOrder(Long id);
}
