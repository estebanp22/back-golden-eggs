package com.goldeneggs.InventoryMovement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.goldeneggs.Egg.Egg;
import com.goldeneggs.Order.Order;
import com.goldeneggs.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Represents a stock entry in the inventory system.
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryMovement implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date the products were added to the inventory.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "movement_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date movementDate;

    @Column(name = "combs")
    private Integer combs;

    /**
     * List of eggs associated with this inventory entry.
     */
    @ManyToOne
    @JoinColumn(name = "egg_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Egg egg;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)//puede no venir de una venta
    @JsonIdentityReference(alwaysAsId = true)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
