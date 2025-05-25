package com.goldeneggs.Dto.Order;

import lombok.Data;

@Data
public class CartItemDTO {
    private String name;    // Tipo de huevo
    private String color;   // Color del huevo
    private int quantity;
    private double price;
}
