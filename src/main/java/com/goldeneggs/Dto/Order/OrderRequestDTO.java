package com.goldeneggs.Dto.Order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private Long idCustomer;
    private List<CartItemDTO> cartItem;
    private double totalPrice;
    private Date orderDate;
    private String state;
}
