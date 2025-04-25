package com.goldeneggs.Order;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing orders.
 * <p>
 * This controller provides endpoints for performing CRUD operations on orders,
 * including retrieving, creating, updating, and deleting orders in the system.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

    /**
     * Constructor to inject the {@link OrderService} dependency.
     *
     * @param orderService The order service used for managing orders.
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retrieves all orders in the system.
     *
     * @return A list of all orders.
     * @throws ServiceException If an error occurs while fetching the orders.
     */
    @GetMapping("/getAll")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Retrieves an order by its ID.
     * <p>
     * If the order with the given ID does not exist, it throws a
     * {@link ResourceNotFoundException}.
     * </p>
     *
     * @param id The ID of the order to retrieve.
     * @return The order if found, otherwise throws a {@link ResourceNotFoundException}.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @GetMapping("/get/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    /**
     * Saves or updates an order.
     * <p>
     * If the order already exists, it is updated; otherwise, a new order is created.
     * </p>
     *
     * @param order The order to save or update.
     * @return The saved or updated order.
     * @throws BadRequestException If the order data is invalid or incomplete.
     */
    @PostMapping("/save")
    public Order saveOrder(@RequestBody Order order) throws BadRequestException {
        return orderService.saveOrder(order);
    }

    /**
     * Deletes an order by its ID.
     * <p>
     * If the order with the given ID does not exist, it throws a
     * {@link ResourceNotFoundException}.
     * </p>
     *
     * @param id The ID of the order to delete.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }
}
