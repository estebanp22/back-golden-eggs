package com.goldeneggs.Dto.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object for representing a customer order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    /**
     * Unique identifier of the order.
     */
    private Long id;

    /**
     * Full name of the customer.
     */
    private String customerName;

    /**
     * Current state of the order (PENDING, PAID, CANCELLED).
     */
    private String status;

    /**
     * Total amount of the order.
     */
    private double total;

    /**
     * Date when the order was created (formatted as yyyy-MM-dd).
     */
    private String date;

    /**
     * List of ordered items.
     */
    private List<OrderItemDTO> items;
}
