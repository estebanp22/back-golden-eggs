package com.goldeneggs.InventoryMovement;

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
    private Date movementDate;

    @Column(name = "honey_combs")
    private Integer honeyCombs;

    /**
     * List of eggs associated with this inventory entry.
     */
    @ManyToOne
    @JoinColumn(name = "egg_id", nullable = false)
    private Egg egg;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)//puede no venir de una venta
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
