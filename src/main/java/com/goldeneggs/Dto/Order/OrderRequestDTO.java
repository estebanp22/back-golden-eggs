package com.goldeneggs.Dto.Order;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class OrderRequestDTO {
    private Long idCustomer;
    private List<CartItemDTO> cartItem;
    private double totalPrice;
    private Date orderDate;
    private String state;
}
