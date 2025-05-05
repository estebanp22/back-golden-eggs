package com.goldeneggs.Egg;

import com.goldeneggs.Inventory.Inventory;
import com.goldeneggs.Supplier.Supplier;
import com.goldeneggs.TypeEgg.TypeEgg;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a batch of eggs of the same type and properties.
 */
@Entity
@Table(name = "eggs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Egg implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Type of egg (e.g., AA, A, B).
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "type_id")
    private TypeEgg type;

    /**
     * Color of the egg shell.
     */
    @Column(nullable = false)
    private String color;

    /**
     * Purchase price of the egg.
     */
    @Column(nullable = false)
    private Double buyPrice;

    /**
     * Sale price of the egg.
     */
    @Column(nullable = true)
    private Double salePrice;

    /**
     * Expiration date of the egg batch.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    /**
     * Supplier that provided this egg batch.
     */
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    /**
     * Inventory entry this egg batch belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    /**
     * Quantity of eggs in this batch.
     */
    @Column(nullable = false)
    private Integer quantity;
}
