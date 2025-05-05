package com.goldeneggs.Order;

import com.goldeneggs.Exception.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.ResponseEntity;
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
     * @return A ResponseEntity containing a list of all orders or no content if empty.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return orders.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(orders);
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id The ID of the order to retrieve.
     * @return A ResponseEntity containing the found order.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Saves or updates an order.
     *
     * @param order The order to save or update.
     * @return A ResponseEntity containing the saved or updated order.
     * @throws BadRequestException If the order data is invalid or incomplete.
     */
    @PostMapping("/save")
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) throws BadRequestException {
        Order savedOrder = orderService.saveOrder(order);
        return ResponseEntity.ok(savedOrder);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id The ID of the order to delete.
     * @return A ResponseEntity with status 204 if successful.
     * @throws ResourceNotFoundException If the order with the given ID does not exist.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the list of orders placed in the current month.
     *
     * @return A ResponseEntity containing a list of orders placed in the current month or a no content response.
     */
    @GetMapping("/currentMonth")
    public ResponseEntity<List<Order>> getOrdersInCurrentMonth() {
        List<Order> orders = orderService.getOrdersInCurrentMonth();
        return orders.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(orders);
    }

    /**
     * Counts the number of orders placed in the current month.
     *
     * @return A ResponseEntity containing the total number of orders placed in the current month.
     */
    @GetMapping("/countCurrentMonth")
    public ResponseEntity<Long> countOrdersInCurrentMonth() {
        Long count = orderService.countOrdersInCurrentMonth();
        return ResponseEntity.ok(count);
    }
}
