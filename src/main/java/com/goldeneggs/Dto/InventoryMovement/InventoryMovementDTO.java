package com.goldeneggs.Dto.InventoryMovement;

import com.goldeneggs.Egg.Egg;
import com.goldeneggs.InventoryMovement.InventoryMovement;
import lombok.Data;

import java.sql.Date;

@Data
public class InventoryMovementDTO {
    private Long id;
    private Date movementDate;
    private Integer combs;
    private Egg egg;

    // Campos específicos de User
    private Long userId;
    private String userName;

    // Campos específicos de Order
    private Double orderTotal;

    public InventoryMovementDTO(InventoryMovement movement) {
        this.id = movement.getId();
        this.movementDate = (Date) movement.getMovementDate();
        this.combs = movement.getCombs();
        this.egg = movement.getEgg();

        // Extrae solo lo necesario de User
        if (movement.getUser() != null) {
            this.userId = movement.getUser().getId();
            this.userName = movement.getUser().getName(); // Asume que User tiene getName()
        }

        // Extrae solo lo necesario de Order
        if (movement.getOrder() != null) {
            this.orderTotal = movement.getOrder().getTotalPrice();
        }
    }
}