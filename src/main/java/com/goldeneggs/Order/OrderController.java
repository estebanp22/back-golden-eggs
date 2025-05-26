package com.goldeneggs.Order;

import com.goldeneggs.Dto.Order.OrderDTO;
import com.goldeneggs.Dto.Order.OrderRequestDTO;
import com.goldeneggs.Exception.InvalidOrderDataException;
import com.goldeneggs.Exception.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * Counts the total number of orders for a specific customer.
     *
     * @param customerId The ID of the customer whose orders should be counted.
     * @return A ResponseEntity containing the total number of orders for the specified customer.
     * @throws ResourceNotFoundException If the customer with the given ID does not exist.
     */
    @GetMapping("/countByCustomer/{customerId}")
    public ResponseEntity<Long> countOrdersByCustomer(@PathVariable Long customerId) {
        Long count = orderService.countOrdersByCustomerId(customerId);
        return ResponseEntity.ok(count);
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
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        }catch (ResourceNotFoundException e) {
            return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Saves or updates an order.
     *
     * @param order The order to save or update.
     * @return A ResponseEntity containing the saved or updated order.
     * @throws BadRequestException If the order data is invalid or incomplete.
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveOrder(@RequestBody OrderRequestDTO order) throws BadRequestException {
        try {
            Order saved =  orderService.saveOrder(order);
            System.out.println();
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        }catch (InvalidOrderDataException e) {
            return new  ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (RuntimeException e){
            return new ResponseEntity<>("Error al procresar la orden:" + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates an existing order.
     *
     * @param id ID of the order to update.
     * @param order Updated order data.
     * @return Updated order entity.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Order order) {
        try{
            Order updated = orderService.updateOrder(id, order);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }catch (ResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }catch (InvalidOrderDataException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok().build();
        }catch (ResourceNotFoundException e){
            return ResponseEntity.noContent().build();
        }
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


    /**
     * Retrieves all orders as Data Transfer Objects (DTOs).
     *
     * @return A ResponseEntity containing a list of OrderDTO objects representing all orders.
     */
    @GetMapping("/getAll/dto")
    public ResponseEntity<List<OrderDTO>> getAllOrdersDTO() {
        List<OrderDTO> orders = orderService.getAllAsDTO();
        return ResponseEntity.ok(orders);
    }

    /**
     * Cancels an existing order by setting its state to "CANCELED".
     *
     * @param id The ID of the order to cancel.
     * @return ResponseEntity with HTTP status 200 if successful, or 404 if the order is not found.
     */
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Processes an order based on the provided order ID and payment method.
     *
     * @param id The ID of the order to be processed.
     * @param payload A map containing the details for processing the order, specifically the "paymentMethod" key.
     * @return A ResponseEntity with an HTTP status of 200 (OK) if the processing is successful.
     */
    @PutMapping("/process/{id}")
    public ResponseEntity<Void> processOrder(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String paymentMethod = payload.get("paymentMethod");
        orderService.processOrder(id, paymentMethod);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getOrdersCustomer/{id}")
    public ResponseEntity<List<OrderRequestDTO>> getOrdersByCustomer(@PathVariable Long id) {
        List<OrderRequestDTO> orders = orderService.getOrdersByCustomerId(id);
        return ResponseEntity.ok(orders);
    }
}
