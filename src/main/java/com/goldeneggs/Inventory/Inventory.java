package com.goldeneggs.Inventory;

import com.goldeneggs.Egg.Egg;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Represents an inventory item in the system.
 */
@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product.
     */
    @Column(name = "name_product", nullable = false)
    private String nameProduct;

    /**
     * Quantity currently available in stock.
     */
    @Column(name = "available_quantity", nullable = false)
    private String availableQuantity;

    /**
     * Unit price of the product.
     */
    @Column(nullable = false)
    private double price;

    /**
     * Date the product was added to inventory.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "entry_date", nullable = false)
    private Date entryDate;

    /**
     * List of eggs associated with this inventory item.
     */
    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Egg> eggs;

}
