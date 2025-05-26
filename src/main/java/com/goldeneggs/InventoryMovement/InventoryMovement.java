package com.goldeneggs.InventoryMovement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Temporal(TemporalType.DATE)
    @Column(name = "movement_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date movementDate;

    @Column(name = "combs")
    private Integer combs;

    @ManyToOne
    @JoinColumn(name = "egg_id", nullable = false)
    @JsonIdentityReference(alwaysAsId = true)
    private Egg egg;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = true)
    @JsonIgnore  // ← No se incluirá en el JSON
    private Order order;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore  // ← No se incluirá en el JSON
    private User user;
}

