package com.goldeneggs.Egg;

import com.goldeneggs.Inventory.Inventory;
import com.goldeneggs.Supplier.Supplier;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an egg with its attributes.
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
     * Type of egg (e.g., organic, cage-free).
     */
    @Column(nullable = false)
    private String type;

    /**
     * Shell color of the egg.
     */
    @Column(nullable = false)
    private String color;

    /**
     * Expiration date of the egg.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

    /**
     * Category or size (e.g., XL, L, M).
     */
    @Column(nullable = false)
    private String category;

    /**
     * Supplier providing the product.
     */
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;


    /**
     * Inventory item to which this egg belongs.
     */
    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

}
