package com.goldeneggs.Dto.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing an item in an order.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDTO {

    /**
     * Name of the egg product.
     */
    private String productName;

    /**
     * Quantity of this item ordered.
     */
    private int quantity;

    /**
     * Unit price of the egg at time of order.
     */
    private double unitPrice;
}
