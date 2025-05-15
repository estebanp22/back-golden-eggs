package com.goldeneggs.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BillDto {
    private Long id;
    private String issueDate;
    private boolean paid;
    private double totalPrice;
    private String customerName;
    private String orderDate;
    private String orderState;


}

